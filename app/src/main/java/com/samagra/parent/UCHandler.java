package com.samagra.parent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.samagra.ancillaryscreens.data.network.model.LoginResponse;
import com.samagra.grove.logging.Grove;

import org.odk.collect.android.BuildConfig;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

import io.sentry.Sentry;
import io.sentry.event.Breadcrumb;
import timber.log.Timber;

public class UCHandler {
    private static String strCurrentErrorLog;
    public static final String EXTRA_STACK_TRACE = "EXTRA_STACK_TRACE";
    public static final String EXTRA_ACTIVITY_LOG = "EXTRA_ACTIVITY_LOG";
    public static final String ACTIVITY_NAME = "ACTIVITY_NAME";
    private static final String UCE_HANDLER_PACKAGE_NAME = "com.samagra.master.sakshamsamiksha";
    private static final String DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os";
    private static final int MAX_STACK_TRACE_SIZE = 131071; //128 KB - 1
    private static final int MAX_ACTIVITIES_IN_LOG = 100;
    private static final String SHARED_PREFERENCES_FILE = "uceh_preferences";
    private static final String SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp";
    private static final Deque<String> activityLog = new ArrayDeque<>(MAX_ACTIVITIES_IN_LOG);
    @SuppressLint("StaticFieldLeak")
    private static MyApplication application;
    private static boolean isInBackground = true;
    private static boolean isBackgroundMode;
    private static boolean isUCEHEnabled;
    private static boolean isTrackActivitiesEnabled;
    private static WeakReference<Activity> lastActivityCreated = new WeakReference<>(null);

    UCHandler(Builder builder) {
        isUCEHEnabled = builder.isUCEHEnabled;
        isTrackActivitiesEnabled = builder.isTrackActivitiesEnabled;
        isBackgroundMode = builder.isBackgroundModeEnabled;
        setUCHandler(builder.context);
    }

    private static void setUCHandler(final Context context) {
        try {
            if (context != null) {
                final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
                if (oldHandler != null && oldHandler.getClass().getName().startsWith(UCE_HANDLER_PACKAGE_NAME)) {
                    Grove.d("UCHandler was already installed, doing nothing!");
                } else {
                    if (oldHandler != null && !oldHandler.getClass().getName().startsWith(DEFAULT_HANDLER_PACKAGE_NAME)) {
                        Grove.d("You already have an UncaughtExceptionHandler. If you use a custom UncaughtExceptionHandler, it should be initialized after UCHandler! Installing anyway, but your original handler will not be called.");
                    }
                    application = (MyApplication) context.getApplicationContext();
                    //Setup UCE Handler.
                    Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                        if (isUCEHEnabled) {
                            Grove.d(throwable, "App crashed, executing UCHandler's UncaughtExceptionHandler");
                            if (hasCrashedInTheLastSeconds(application)) {
                                Grove.d(throwable, "App already crashed recently, not starting custom error activity because we could enter a restart loop. Are you sure that your app does not crash directly on init?");
                                if (oldHandler != null) {
                                    oldHandler.uncaughtException(thread, throwable);
                                    return;
                                }
                            } else {
                                setLastCrashTimestamp(application, new Date().getTime());
                                if (!isInBackground || isBackgroundMode) {
                                    final Intent intent = new Intent(application, LoginResponse.class);
                                    StringWriter sw = new StringWriter();
                                    PrintWriter pw = new PrintWriter(sw);
                                    throwable.printStackTrace(pw);
                                    String stackTraceString = sw.toString();
                                    if (stackTraceString.length() > MAX_STACK_TRACE_SIZE) {
                                        String disclaimer = " [stack trace too large]";
                                        stackTraceString = stackTraceString.substring(0, MAX_STACK_TRACE_SIZE - disclaimer.length()) + disclaimer;
                                    }
                                    intent.putExtra(EXTRA_STACK_TRACE, stackTraceString);
                                    intent.putExtra(ACTIVITY_NAME, throwable.toString());
                                    if (isTrackActivitiesEnabled) {
                                        StringBuilder activityLogStringBuilder = new StringBuilder();
                                        while (!activityLog.isEmpty()) {
                                            activityLogStringBuilder.append(activityLog.poll());
                                        }
                                        intent.putExtra(EXTRA_ACTIVITY_LOG, activityLog.toString());
                                    }
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    List<Breadcrumb> breadcrumbs = Sentry.getContext().getBreadcrumbs();
                                    StringBuilder logs = new StringBuilder();
                                    for (Breadcrumb breadcrumb : breadcrumbs) {
                                        logs.append(breadcrumb.getMessage()).append("\t").append(breadcrumb.getTimestamp().toString()).append("\n");
                                    }
                                    intent.putExtra("LOGS", logs.toString());
                                    sendEmail(intent, context);
                                    //application.startActivity(intent);
                                } else {
                                    if (oldHandler != null) {
                                        oldHandler.uncaughtException(thread, throwable);
                                        return;
                                    }
                                    //If it is null (should not be), we let it continue and kill the process or it will be stuck
                                }
                            }
                            final Activity lastActivity = lastActivityCreated.get();
                            if (lastActivity != null) {
                                lastActivity.finish();
                                lastActivityCreated.clear();
                            }
                            killCurrentProcess();
                        } else if (oldHandler != null) {
                            //Pass control to old uncaught exception handler
                            oldHandler.uncaughtException(thread, throwable);
                        }
                    });
                    application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        int currentlyStartedActivities = 0;

                        @Override
                        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                            if (isTrackActivitiesEnabled) {
                                activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " created\n");
                            }
                        }

                        @Override
                        public void onActivityStarted(Activity activity) {
                            currentlyStartedActivities++;
                            isInBackground = (currentlyStartedActivities == 0);
                        }

                        @Override
                        public void onActivityResumed(Activity activity) {
                            if (isTrackActivitiesEnabled) {
                                activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " resumed\n");
                            }
                        }

                        @Override
                        public void onActivityPaused(Activity activity) {
                            if (isTrackActivitiesEnabled) {
                                activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " paused\n");
                            }
                        }

                        @Override
                        public void onActivityStopped(Activity activity) {
                            currentlyStartedActivities--;
                            isInBackground = (currentlyStartedActivities == 0);
                        }

                        @Override
                        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                        }

                        @Override
                        public void onActivityDestroyed(Activity activity) {
                            if (isTrackActivitiesEnabled) {
                                activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " destroyed\n");
                            }
                        }
                    });
                }
                Timber.i("UCHandler has been installed.");
            } else {
                Grove.d("Context can not be null");
            }
        } catch (Throwable throwable) {
            Grove.d(throwable, "UCHandler can not be initialized. Help making it better by reporting this as a bug.");
        }
    }

    private static void sendEmail(Intent intent, Context context) {
        // REPLACE WITH YOUR IDENTITY POOL AND REGION
        // LOADS CREDENTIALS FROM AWS COGNITO IDENTITY POOL
        CognitoCachingCredentialsProvider credentials = new CognitoCachingCredentialsProvider(
                context, // CONTEXT
                "us-west-2:213e041d-1a39-4006-90eb-e7fa28d6a41b", // IDENTITY POOL ID
                Regions.US_WEST_2 // REGION
        );

        // CREATES SES CLIENT TO MANAGE SENDING EMAIL
        final AmazonSimpleEmailServiceClient ses = new AmazonSimpleEmailServiceClient(credentials);
        ses.setRegion(Region.getRegion(Regions.US_WEST_2));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sharedPreferences.getString("user.fullName", "");
        String[] lines = getStackTraceFromIntent(intent).split(":");
        String mailSubject = "[CTT]  " + getApplicationName(context).toUpperCase() + " User: " + userName + " - ver(" + BuildConfig.VERSION_CODE + ") - " + lines[0].substring(10) + ":" + lines[1];
        Content subject = new Content(mailSubject);
        Body body = new Body(new Content(getAllErrorDetailsFromIntent(context, intent)));
        final Message message = new Message(subject, body);
        final String from = "test@samagragovernance.in";
        String to = "chakshu@samagragovernance.in, umangbhola@samagragovernance.in";

        final Destination destination = new Destination()
                .withToAddresses(to.contentEquals("") ? null : Arrays.asList(to.split("\\s*,\\s*")));

        // CREATES SEPARATE THREAD TO ATTEMPT TO SEND EMAIL
        Thread sendEmailThread = new Thread(() -> {
            try {
                SendEmailRequest request = new SendEmailRequest(from, destination, message);
                ses.sendEmail(request);
            } catch (Exception e) {
                //No method
            }
        });

        // RUNS SEND EMAIL THREAD
        sendEmailThread.start();

        try {
            // WAITS THREAD TO COMPLETE TO ACT ON RESULT
            sendEmailThread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static String getStackTraceFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_STACK_TRACE);
    }


    private static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "Unknown";
        }
    }


    private static String getAllErrorDetailsFromIntent(Context context, Intent intent) {
        if (TextUtils.isEmpty(strCurrentErrorLog)) {
            String LINE_SEPARATOR = "\n";
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String userName = sharedPreferences.getString("user.fullName", "");
            StringBuilder errorReport = new StringBuilder();
            errorReport.append("\n***** Error Title \n");
            errorReport.append(getApplicationName(context));
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Error: ");
            String[] lines = getStackTraceFromIntent(intent).split(":");
            if (lines[0].contains("java.lang."))
                errorReport.append(lines[0].substring(10));
            String versionName = getVersionName(context);
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

    private static String getActivityLogFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_ACTIVITY_LOG);
    }

    private static String getActivityName(Intent intent) {
        return intent.getStringExtra(ACTIVITY_NAME);
    }


    private static String getFirstInstallTimeAsString(Context context, DateFormat dateFormat) {
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

    private static String getLastUpdateTimeAsString(Context context, DateFormat dateFormat) {
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


    private static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    /**
     * INTERNAL method that tells if the app has crashed in the last seconds.
     * This is used to avoid restart loops.
     *
     * @return true if the app has crashed in the last seconds, false otherwise.
     */
    private static boolean hasCrashedInTheLastSeconds(Context context) {
        long lastTimestamp = getLastCrashTimestamp(context);
        long currentTimestamp = new Date().getTime();
        return (lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < 3000);
    }

    @SuppressLint("ApplySharedPref")
    private static void setLastCrashTimestamp(Context context, long timestamp) {
        context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, timestamp).commit();
    }

    private static void killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private static long getLastCrashTimestamp(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, -1);
    }

    public static class Builder {
        private Context context;
        private boolean isUCEHEnabled = true;
        private boolean isTrackActivitiesEnabled = false;
        private boolean isBackgroundModeEnabled = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTrackActivitiesEnabled(boolean isTrackActivitiesEnabled) {
            this.isTrackActivitiesEnabled = isTrackActivitiesEnabled;
            return this;
        }

        public Builder setBackgroundModeEnabled(boolean isBackgroundModeEnabled) {
            this.isBackgroundModeEnabled = isBackgroundModeEnabled;
            return this;
        }

        public UCHandler build() {
            return new UCHandler(this);
        }
    }
}
