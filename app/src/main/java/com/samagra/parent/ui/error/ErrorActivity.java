package com.samagra.parent.ui.error;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.samagra.grove.logging.Grove;
import com.samagra.grove.logging.SendEmailTask;
import com.samagra.parent.BuildConfig;
import com.samagra.parent.MyApplication;
import com.samagra.parent.R;
import com.samagra.parent.ui.splash.SplashActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class ErrorActivity extends AppCompatActivity {

    private String strCurrentErrorLog;
    private SharedPreferences sharedPreferences;
    public static final String EXTRA_STACK_TRACE = "EXTRA_STACK_TRACE";
    public static final String EXTRA_ACTIVITY_LOG = "EXTRA_ACTIVITY_LOG";
    public static final String ACTIVITY_NAME = "ACTIVITY_NAME";
    private String mailSubject; 
    private String appName;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_show_error);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ErrorActivity.this);
        appName = "com.samagra.sakshamSamiksha";
        Grove.e("Starting Error Activity");
        sendEmail();

        findViewById(R.id.restartApp_button).setOnClickListener(v -> {
            PendingIntent intent = PendingIntent.getActivity(
                    getApplication().getBaseContext(),
                    0,
                    new Intent(getIntent()),
                    getIntent().getFlags());
            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, intent);
            System.exit(2);
//            Intent mStartActivity = new Intent(ErrorActivity.this, SplashActivity.class);
//            int mPendingIntentId = 123456;
//            PendingIntent mPendingIntent = PendingIntent.getActivity(ErrorActivity.this, mPendingIntentId, mStartActivity,
//                    PendingIntent.FLAG_CANCEL_CURRENT);
//            AlarmManager mgr = (AlarmManager) ErrorActivity.this.getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//            System.exit(0);
        });


    }

    private String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getActivityLogFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_ACTIVITY_LOG);
    }

    private String getStackTraceFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_STACK_TRACE);
    }

    private String getActivityName(Intent intent) {
        return intent.getStringExtra(ACTIVITY_NAME);
    }

    private String getAllErrorDetailsFromIntent(Context context, Intent intent) {
        if (TextUtils.isEmpty(strCurrentErrorLog)) {
            String LINE_SEPARATOR = "\n";
            String userName = sharedPreferences.getString("user.fullName", "");
            StringBuilder errorReport = new StringBuilder();
            errorReport.append("\n***** Error Title \n");
            errorReport.append(appName);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Error: ");
            String[] lines = getStackTraceFromIntent(intent).split(":");
            if(lines[0].contains("java.lang."))
            errorReport.append(lines[0].substring(10));
            String versionName = getVersionName(context);
            String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
            mailSubject = "[CTT]  "+appName.toUpperCase() + " User: " + userName + " - ver(" + versionCode+ ") - " +lines[0].substring(10)+ ":" +lines[1];

            errorReport.append(lines[1]);
            String[] line = lines[3].split("at");
            errorReport.append(LINE_SEPARATOR);
            errorReport.append(line[0]);
            errorReport.append(LINE_SEPARATOR);

            errorReport.append("\n***** BreadCrumbs \n");
            errorReport.append(intent.getStringExtra("LOGS"));

            errorReport.append("\n***** USER INFO \n");
            errorReport.append("Name: ");
            errorReport.append(userName);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("User Data: ");
            errorReport.append(sharedPreferences.getString("user.data", ""));
            errorReport.append(LINE_SEPARATOR);
            errorReport.append(sharedPreferences.getString("user.data", ""));
            errorReport.append(LINE_SEPARATOR);

            errorReport.append("\n***** DEVICE INFO \n");
            errorReport.append("Brand: ");
            errorReport.append(Build.BRAND);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Device: ");
            errorReport.append(Build.DEVICE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Model: ");
            errorReport.append(Build.MODEL);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Manufacturer: ");
            errorReport.append(Build.MANUFACTURER);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Product: ");
            errorReport.append(Build.PRODUCT);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("SDK: ");
            errorReport.append(Build.VERSION.SDK_INT);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Release: ");
            errorReport.append(Build.VERSION.RELEASE);
            errorReport.append(LINE_SEPARATOR);

            errorReport.append("\n***** APP INFO \n");
            errorReport.append("Version: ");
            errorReport.append(versionName);
            errorReport.append(LINE_SEPARATOR);
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String firstInstallTime = getFirstInstallTimeAsString(context, dateFormat);
            if (!TextUtils.isEmpty(firstInstallTime)) {
                errorReport.append("Installed On: ");
                errorReport.append(firstInstallTime);
                errorReport.append(LINE_SEPARATOR);
            }
            String lastUpdateTime = getLastUpdateTimeAsString(context, dateFormat);
            if (!TextUtils.isEmpty(lastUpdateTime)) {
                errorReport.append("Updated On: ");
                errorReport.append(lastUpdateTime);
                errorReport.append(LINE_SEPARATOR);
            }
            errorReport.append("Current Date: ");
            errorReport.append(dateFormat.format(currentDate));
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("\n***** ERROR LOG \n");
            errorReport.append(getStackTraceFromIntent(intent));
            errorReport.append(LINE_SEPARATOR);
            String activityLog = getActivityLogFromIntent(intent);
            errorReport.append(getActivityName(intent));
            errorReport.append(LINE_SEPARATOR);
            if (activityLog != null) {
                errorReport.append("\n***** USER ACTIVITIES \n");
                errorReport.append("User Activities: ");
                errorReport.append(activityLog);
                errorReport.append(LINE_SEPARATOR);
            }
            errorReport.append("\n***** END OF LOG *****\n");
            strCurrentErrorLog = errorReport.toString();
        }
        return strCurrentErrorLog;
    }

    private String getFirstInstallTimeAsString(Context context, DateFormat dateFormat) {
        long firstInstallTime;
        try {
            firstInstallTime = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .firstInstallTime;
            return dateFormat.format(new Date(firstInstallTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private String getLastUpdateTimeAsString(Context context, DateFormat dateFormat) {
        long lastUpdateTime;
        try {
            lastUpdateTime = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .lastUpdateTime;
            return dateFormat.format(new Date(lastUpdateTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
    
    private void sendEmail() {
        CognitoCachingCredentialsProvider credentials = new CognitoCachingCredentialsProvider(
                ErrorActivity.this, // CONTEXT
                "us-west-2:213e041d-1a39-4006-90eb-e7fa28d6a41b", // IDENTITY POOL ID
                Regions.US_WEST_2 // REGION
        );

        // CREATES SES CLIENT TO MANAGE SENDING EMAIL
        final AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(credentials);
        ses.setRegion(Region.getRegion(Regions.US_WEST_2));
        String[] lines = getStackTraceFromIntent(getIntent()).split(":");
        String versionCode = String.valueOf(org.odk.collect.android.BuildConfig.VERSION_CODE);
        String userName = sharedPreferences.getString("user.fullName", "");
        mailSubject = "[CTT]  "+appName.toUpperCase() + " User: " + userName +
                " - ver(" + versionCode+ ") - " +lines[0].substring(10)+ ":" +lines[1];
        Content subject = new Content(mailSubject);
        Body body = new Body(new Content(getAllErrorDetailsFromIntent(ErrorActivity.this,getIntent())));
        final Message message = new Message(subject, body);
        final String from = "test@samagragovernance.in";
        String to = "umangbhola@samagragovernance.in";

        final Destination destination = new Destination()
                .withToAddresses(to.contentEquals("") ? null : Arrays.asList(to.split("\\s*,\\s*")));

        new SendEmailTask(to, from, destination, message, credentials, s -> Grove.e("Email sent with response: "+ s)).execute();
    }

}
