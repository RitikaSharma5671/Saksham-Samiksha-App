package com.samagra.parent.ui.HomeScreen;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.androidnetworking.AndroidNetworking;
import com.example.assets.uielements.SamagraAlertDialog;
import com.example.student_details.contracts.IStudentDetailsContract;
import com.example.student_details.contracts.StudentDetailsComponentManager;
import com.example.student_details.ui.teacher_aggregate.MainActivity;
import com.example.update.UpdateApp;
import com.google.android.material.snackbar.Snackbar;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.models.AboutBundle;
import com.samagra.ancillaryscreens.screens.passReset.OTPActivity;
import com.samagra.ancillaryscreens.screens.profile.UserProfileElement;
import com.samagra.cascading_module.CascadingModuleDriver;
import com.samagra.commons.Constants;
import com.samagra.commons.CustomEvents;
import com.samagra.commons.ExchangeObject;
import com.samagra.commons.FormFilledEvent;
import com.samagra.commons.InstitutionInfo;
import com.samagra.commons.InternetMonitor;
import com.samagra.commons.MainApplication;
import com.samagra.commons.Modules;
import com.samagra.commons.notifications.AppNotificationUtils;
import com.samagra.grove.logging.Grove;
import com.samagra.parent.AppConstants;
import com.samagra.parent.R;
import com.samagra.parent.UtilityFunctions;
import com.samagra.parent.base.BaseActivity;

import org.odk.collect.android.application.Collect1;
import org.odk.collect.android.utilities.LocaleHelper;
import org.odk.collect.android.utilities.PermissionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.samagra.commons.notifications.AppNotificationUtils.DAILY_REMINDER_REQUEST_CODE;

/**
 * View part of the Home Screen. This class only handles the UI operations, all the business logic is simply
 * abstracted from this Activity. It <b>must</b> implement the {@link HomeMvpView} and extend the {@link BaseActivity}
 *
 * @author Pranav Sharma
 */
public class HomeActivity extends BaseActivity implements HomeMvpView, IHomeItemClickListener {

    private RelativeLayout parent;
    private RelativeLayout progressBarLayout;
    private TextView progressBarText;
    private ProgressBar lottie_loader;
    private RelativeLayout parentHome;
    private TextView welcomeText;
    private RecyclerView homeRecyclerView;
    private boolean shouldShow = true;
    private Disposable logoutListener = null;
    private static CompositeDisposable formSentDisposable = new CompositeDisposable();
    private PopupMenu popupMenu;
    private Snackbar messageView = null;
    UpdateApp mUpdateApp;
    @Inject
    HomePresenter<HomeMvpView, HomeMvpInteractor> homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        parent = findViewById(R.id.parent);
        progressBarLayout = findViewById(R.id.progress_bar_layout);
        progressBarText = findViewById(R.id.progress_bar_text);
        lottie_loader = findViewById(R.id.lottie_loader);
        parentHome = findViewById(R.id.parentHome);
        welcomeText = findViewById(R.id.welcome_text);
        homeRecyclerView = findViewById(R.id.home_items_layout);
        getActivityComponent().inject(this);
        homePresenter.onAttach(this);
        HomeItemsAdapter homeItemsAdapter = new HomeItemsAdapter(
                this,
                homePresenter.fetchHomeItemList(),
                getActivityContext()
        );
        homeRecyclerView.setAdapter(homeItemsAdapter);
        setupToolbar();
        homePresenter.applySettings();
        homePresenter.setStudentData();
        InternetMonitor.startMonitoringInternet(((MainApplication) getApplicationContext()));
        homePresenter.updateLanguageSettings();
        AppNotificationUtils.updateFirebaseToken(getActivityContext(), AppConstants.BASE_API_URL, getActivityContext().getResources().getString(R.string.fusionauth_api_key));
        homePresenter.fetchStudentData(getActivityContext());
        homePresenter.fetchSchoolEmployeeData(getActivityContext());
        renderLayoutInvisible();
        setReminder();
    }

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    private void scheduleNotification (Notification notification , int delay) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        notificationIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;

        long futureInMillis = SystemClock. elapsedRealtime () + delay ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
//        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,SystemClock. elapsedRealtime () + 10000,
                10000, pendingIntent);
    }
    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }
    public void setReminder()
    {
//        scheduleNotification(getNotification( "10 second delay" ) , 10000 ) ;


//        Constraints constraints = new Constraints.Builder().setRequiresCharging(true).setRequiredNetworkType(NetworkType.UNMETERED).build();

//        final PeriodicWorkRequest periodicWorkRequest1 = new PeriodicWorkRequest.
//                Builder(BackgroundTaskJava.class,20, TimeUnit.MINUTES)
//                .setInitialDelay(6000,TimeUnit.MILLISECONDS)
//                .build();
//
//        WorkManager workManager =  WorkManager.getInstance(this);
//
//        workManager.enqueue(periodicWorkRequest1);
//
//        workManager.getWorkInfoByIdLiveData(periodicWorkRequest1.getId())
//                .observe(this, new Observer<WorkInfo>() {
//                    @Override
//                    public void onChanged(@Nullable WorkInfo workInfo) {
//                        if (workInfo != null) {
//                            Log.d("periodicWorkRequest", "Status changed to : " + workInfo.getState());
//
//                        }
//                    }
//                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ReminderWorker.Companion.runAt();
        }
//        Calendar calendar = Calendar.getInstance();
////        Calendar setcalendar = Calendar.getInstance();
////        setcalendar.set(Calendar.HOUR_OF_DAY, 16);
////        setcalendar.set(Calendar.MINUTE, 15);
////        setcalendar.set(Calendar.SECOND, 0);
//        // cancel already scheduled reminders
//        cancelReminder(getActivityContext(),HomeActivity.class);
//
////        if(setcalendar.before(calendar))
////            setcalendar.add(Calendar.DATE,1);
//
//        // Enable a receiver
//        ComponentName receiver = new ComponentName(getActivityContext(),HomeActivity.class);
//        PackageManager pm = getActivityContext().getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
//
//        Intent intent1 = new Intent(getActivityContext(),HomeActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivityContext(),
//                DAILY_REMINDER_REQUEST_CODE, intent1,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager am = (AlarmManager) getActivityContext().getSystemService(ALARM_SERVICE);
//        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis() + 10000,
//                calendar.getTimeInMillis() + 10000, pendingIntent);


//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(this,AlarmReceiver.class);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DAILY_REMINDER_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 4000, pendingIntent);
    }

    private void showUpdateMobileNumberDialog() {
        if (!homePresenter.hasSeenDialog()) {
            new SamagraAlertDialog.Builder(getActivityContext()).setTitle(getText(R.string.profile_incomplete)).
                    setMessage(getText(R.string.please_update_the_password_and_details_of_the_school_in_charge))
                    .setAction2(getText(R.string.update_details), (actionIndex, alertDialog) -> {
//                        ArrayList<UserProfileElement> profileElements = homePresenter.getProfileConfig();
//                        AncillaryScreensDriver.launchProfileActivity(this, profileElements, homePresenter.fetchUserID());
                        alertDialog.dismiss();
                        Intent otpIntent = new Intent(this, OTPActivity.class);
//                        otpIntent.putExtra("phoneNumber", phoneNumber);
                        otpIntent.putExtra("last", "home");
                        startActivity(otpIntent);
//                        EnterMobileNumberFragment_NewUser enterMobileNumberFragment_newUser = new EnterMobileNumberFragment_NewUser();
//                        addFragment(R.id.fragment_container, getSupportFragmentManager(), enterMobileNumberFragment_newUser, "EnterMobileNumberFragment_NewUser");
//                            parentHome.setVisibility(View.GONE);
                    }).show();
            homePresenter.updateSeenDialogCount();
        }
    }

    private void relaunchHomeScreen() {
        new LocaleHelper().updateLocale(getActivityContext());
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(0, 0);
        finishAffinity();
    }

    @Override
    public void setDownloadProgress(int progress) {
        if (progress >= 100) {
            progressBarText.setText(R.string.hundred_percent);
//            boolean isProfileComplete = homePresenter.isProfileComplete();
//            if (!isProfileComplete) {
//                showUpdateMobileNumberDialog();
//            }
        } else {
            progressBarText.setText(String.format(Locale.ENGLISH, "%d%%", progress));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        renderLayoutInvisible();
        homePresenter.fetchWelcomeText();
        homePresenter.resetProgressVariables();
        boolean isStoragePermissionAvailable = PermissionUtils.areStoragePermissionsGranted(getActivityContext());
        homePresenter.checkForFormUpdates(isStoragePermissionAvailable, getActivityContext());
        homePresenter.checkForDownloadStudentData(getActivityContext());
        customizeToolbar();
        setDisposable();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.getBackStackEntryAt(0);
            if (fm.getBackStackEntryAt(0).getName() != null && (fm.getBackStackEntryAt(0).getName().equals("UpdateAppLanguageFragment")
            ||  fm.getBackStackEntryAt(0).getName().equals("OTPViewFragment") ||
                    fm.getBackStackEntryAt(0).getName().equals("EnterMobileNumberFragment_NewUser"))) {
                fm.popBackStackImmediate();
                parentHome.setVisibility(View.VISIBLE);
            } else {
                super.onBackPressed();
            }

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void renderLayoutVisible() {
        startWalletLoader(false);
        progressBarLayout.setVisibility(View.GONE);
        parentHome.setVisibility(View.VISIBLE);
        if(!homePresenter.isProfileComplete())
            showUpdateMobileNumberDialog();
        if(shouldShow) {
            mUpdateApp = UpdateApp.Builder(this);
            shouldShow = false;
        }
    }


    @Override
    public void renderLayoutInvisible() {
        progressBarLayout.setVisibility(View.VISIBLE);
        startWalletLoader(true);
        parentHome.setVisibility(View.GONE);
    }

    private void startWalletLoader(boolean b) {
        if (b) {
            lottie_loader.setVisibility(View.VISIBLE);
        } else {
            lottie_loader.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void addFragment(int containerViewId, FragmentManager manager, Fragment fragment, String fragmentTag) {
        try {
            final String fragmentName = fragment.getClass().getName();
            Grove.d("addFragment() :: Adding new fragment %s", fragmentName);
            // Create new fragment and transaction
            final FragmentTransaction transaction = manager.beginTransaction();

            transaction.add(containerViewId, fragment, fragmentTag);
            transaction.addToBackStack(fragmentTag);
            new Handler().post(() -> {
                try {
                    transaction.commit();
                } catch (IllegalStateException ex) {
                    Grove.e("Failed to commit Fragment Transaction with exception %s", ex.getMessage());
                }
            });
        } catch (IllegalStateException ex) {
            Grove.e("Failed to add Fragment with exception %s", ex.getMessage());

        }

    }

    private void setDisposable() {
        formSentDisposable
                .add(((MainApplication) (getApplicationContext()))
                        .eventBusInstance()
                        .register(FormFilledEvent.class)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(event -> {
                            String snackbarMessage = homePresenter.isNetworkConnected() ?
                                    getString(R.string.form_submitted_online) :
                                    getString(R.string.form_submitted_offline);
                            UtilityFunctions.showLongSnackbar(parent, snackbarMessage);
                        }));
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(messageView != null)
            messageView.dismiss();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(messageView != null)
            messageView.dismiss();
        if (logoutListener != null && !logoutListener.isDisposed()) {
            AndroidNetworking.cancel(Constants.LOGOUT_CALLS);
            logoutListener.dispose();
        }
        if (formSentDisposable != null && !formSentDisposable.isDisposed()) {
            formSentDisposable.dispose();
        }
        homePresenter.onDetach();
    }

    /**
     * Only set the title and action bar here; do not make further modifications.
     * Any further modifications done to the toolbar here will be overwritten. If you wish to prevent modifications
     * from being overwritten, do them after onCreate is complete.
     * This method should be called in onCreate of your activity.
     */
    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(R.string.samagra_shiksha);
        setSupportActionBar(toolbar);
        toolbar.setNavigationContentDescription("Hello");
    }

    @Override
    public void customizeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(this::initAndShowPopupMenu);
    }

    /**
     * Provides with a {@link AboutBundle} object that is used to further configure
     * the UI for {@link com.samagra.ancillaryscreens.screens.about.AboutActivity}
     */
    private AboutBundle provideAboutBundle() {
        return new AboutBundle(
                getActivityContext().getResources().getString(R.string.about_us),
                AppConstants.ABOUT_WEBSITE_LINK,
                AppConstants.ABOOUT_FORUM_LINK,
                R.drawable.saksham_notif_icon,
                R.string.samagra_shiksha,
                R.string.about_us_summary);
    }

    /**
     * Giving Control of the UI to XML file for better customization and easier changes
     */
    private void initAndShowPopupMenu(View v) {

        if (popupMenu == null) {
            popupMenu = new PopupMenu(HomeActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.home_screen_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
//                    case R.id.change_lang:
//                        if (HomeActivity.this.findViewById(R.id.fragment_container) != null) {
//                            UpdateAppLanguageFragment firstFragment = UpdateAppLanguageFragment.newInstance(PreferenceManager.getDefaultSharedPreferences(HomeActivity.this.getActivityContext())
//                                    .getString(Constants.APP_LANGUAGE_KEY, "en"), language -> {
//                                SharedPreferences.Editor edit = PreferenceManager
//                                        .getDefaultSharedPreferences(getActivityContext()).edit();
//                                edit.putString(KEY_APP_LANGUAGE, language);
//                                edit.putString(Constants.APP_LANGUAGE_KEY, language);
//                                edit.apply();
//                                relaunchHomeScreen();
//                            });
//                            HomeActivity.this.addFragment(R.id.fragment_container, HomeActivity.this.getSupportFragmentManager(), firstFragment, "UpdateAppLanguageFragment");
//                            parentHome.setVisibility(View.GONE);
//                        }
//                        break;
                    case R.id.about_us:
                        AncillaryScreensDriver.launchAboutActivity(HomeActivity.this, HomeActivity.this.provideAboutBundle());
                        break;
                    case R.id.tutorial_video:
                        homePresenter.onViewHelplineClicked();
                        break;
                    case R.id.profile:
                        Grove.d("User clicked on View Profile Section option");
                        ArrayList<UserProfileElement> profileElements = homePresenter.getProfileConfig();
                        AncillaryScreensDriver.launchProfileActivity(this, profileElements, homePresenter.fetchUserID());
                        break;
                    case R.id.logout:
                        Grove.d("User clicked to logout out of application");
                        if (homePresenter.isNetworkConnected()) {
                            if (logoutListener == null) {
                                Grove.d("Logout Listener initialised");
                                HomeActivity.this.initializeLogoutListener();
                            }
                            AncillaryScreensDriver.performLogout(HomeActivity.this, HomeActivity.this.getActivityContext().getResources().getString(R.string.fusionauth_api_key));
                        } else {
                            showSnackbar("It seems you are offline. Logout cannot happen in offline conditions.", 3000);
                        }
                        break;
                }
                return true;
            });
        }
        popupMenu.show();
    }


    private void initializeLogoutListener() {
        Grove.d("Inside Logout initialisation method....");
        logoutListener = ((MainApplication) (getApplicationContext()))
                .getEventBus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    Grove.d("Received event Logout");
                    if (o instanceof ExchangeObject.EventExchangeObject) {
                        ExchangeObject.EventExchangeObject eventExchangeObject = (ExchangeObject.EventExchangeObject) o;
                        if (eventExchangeObject.to == Modules.MAIN_APP && eventExchangeObject.from == Modules.ANCILLARY_SCREENS) {
                            if (eventExchangeObject.customEvents == CustomEvents.LOGOUT_COMPLETED) {
                                IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
                                iStudentDetailsContract.removeRealsmDB();
                                Grove.d("Logout completed Event received");
                                hideLoading();
                                Grove.d("Logout snack bar hidden");
                                logoutListener.dispose();
                                Grove.d("Logout listener disposed off...");
                            } else if (eventExchangeObject.customEvents == CustomEvents.LOGOUT_INITIATED) {
                                Grove.d("Logout initiated Event received");
                                showLoading(getString(R.string.logging_out_message));
                            }
                        }
                    }
                }, Grove::e);
    }

    @Override
    public void showLoading(String message) {
        hideLoading();
        if (messageView == null) {
            messageView = UtilityFunctions.getSnackbarWithProgressIndicator(findViewById(android.R.id.content), getApplicationContext(), message);
        }
        messageView.setText(message);
        messageView.show();
    }

    @Override
    public void hideLoading() {
        if (messageView != null && messageView.isShownOrQueued())
            messageView.dismiss();
    }

    @Override
    public void launchSearchModule() {
        CascadingModuleDriver.init((MainApplication) getApplicationContext(), AppConstants.FILE_PATH, Collect1.getInstance().getStoragePathProvider().getScopedStorageRootDirPath());
        CascadingModuleDriver.launchSearchView(getActivityContext(),
                this, CascadingModuleDriver.SEARCH_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void updateLocale(String language) {
        new LocaleHelper().updateLocale(getActivityContext(), language);

    }


    @Override
    public void displayHomeWelcomeText(String userName) {
        welcomeText.setText(String.format("%s  %s", getActivityContext().getResources().getString(R.string.welcome), userName));
    }

    @Override
    public void showNoInternetMessage() {
        showSnackbar(getActivityContext().getResources().getString(R.string.form_download_error_no_internet_connection), Snackbar.LENGTH_LONG);
    }

    @Override
    public void showDownloadFailureMessage() {
        showSnackbar(getActivityContext().getResources().getString(R.string.form_download_error), Snackbar.LENGTH_LONG);
    }

    @Override
    public void showFailureDownloadMessage() {
        showSnackbar(getActivityContext().getResources().getString(R.string.form_list_download_error), Snackbar.LENGTH_LONG);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CascadingModuleDriver.SEARCH_ACTIVITY_REQUEST_CODE) {
            Grove.d("Received result from Search Activity...");
            String selectedDistrict = data.getStringExtra("selectedDistrict");
            String selectedBlock = data.getStringExtra("selectedBlock");
            String selectedSchool = data.getStringExtra("selectedSchool");
            InstitutionInfo institutionInfo = new InstitutionInfo(selectedDistrict, selectedBlock, selectedSchool);
            Grove.d("Selected District Name is >> " + selectedDistrict + "  Selected Block name is >> " + selectedBlock + "  Selected school name is >> " + selectedSchool);
            homePresenter.prefillData(institutionInfo);
            homePresenter.onFillFormsOptionClicked();
        }
    }

    @Override
    public void onFillFormsClicked() {
        homePresenter.searchmodule();
    }

    @Override
    public void onViewHelplineClicked() {
        homePresenter.onViewHelplineClicked();
    }

    @Override
    public void onSubmitOfflineFormsClicked() {
        homePresenter.onSubmitFormsClicked();
    }

    @Override
    public void onViewODKSubmissionsClicked() {
        homePresenter.onViewSubmittedFormsOptionsClicked(getActivityContext());
    }

    @Override
    public void onEditStudentDataClicked() {
        IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
        iStudentDetailsContract.viewStudentData(getActivityContext(), R.id.fragment_container, getSupportFragmentManager());

    }

    @Override
    public void onMarkStudentAttendanceClicked() {
        IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
        iStudentDetailsContract.markStudentAttendance(getActivityContext(), R.id.fragment_container, getSupportFragmentManager());
    }

    @Override
    public void onViewStudentAttendanceClicked() {
        Intent i = new Intent(getActivityContext(), MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onMarkTeacherAttendanceClicked() {
        IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
        iStudentDetailsContract.markTeacherAttendance(getActivityContext(), R.id.fragment_container, getSupportFragmentManager());

    }

    @Override
    public void onViewSchoolAttendanceClicked() {
        IStudentDetailsContract iStudentDetailsContract1 = StudentDetailsComponentManager.iStudentDetailsContract;
        iStudentDetailsContract1.launchStudentAttendanceView(getActivityContext());
    }

    @Override
    public void onViewTeacherAttendanceClicked() {
        IStudentDetailsContract iStudentDetailsContract1 = StudentDetailsComponentManager.iStudentDetailsContract;
        iStudentDetailsContract1.launchTeacherAttendanceView(getActivityContext());
    }


    @Override
    public void onReportCOVIDCaseClicked() {

    }


}