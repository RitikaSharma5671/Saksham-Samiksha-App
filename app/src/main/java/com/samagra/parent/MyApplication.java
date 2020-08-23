package com.samagra.parent;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.update.UpdateDriver;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.di.FormManagementCommunicator;
import com.samagra.commons.CommonUtilities;
import com.samagra.commons.EventBus;
import com.samagra.commons.ExchangeObject;
import com.samagra.commons.InternetMonitor;
import com.samagra.commons.InternetStatus;
import com.samagra.commons.MainApplication;
import com.samagra.commons.Modules;
import com.samagra.commons.NetworkConnectionInterceptor;
import com.samagra.commons.RxBus;
import com.samagra.commons.utils.AlertDialogUtils;

import com.samagra.grove.contracts.GroveLoggingComponentLauncher;
import com.samagra.grove.contracts.IGroveLoggingComponent;
import com.samagra.grove.contracts.LoggingComponentManager;
import com.samagra.grove.logging.Grove;
import com.samagra.grove.logging.LoggableApplication;
import com.samagra.commons.notifications.AppNotificationUtils;
import com.samagra.parent.di.component.ApplicationComponent;
import com.samagra.parent.di.component.DaggerApplicationComponent;
import com.samagra.parent.di.modules.ApplicationModule;
import com.samagra.parent.helper.OkHttpClientProvider;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.application.FormManagmentModuleInitialisationListener;
import org.odk.collect.android.contracts.ComponentManager;
import org.odk.collect.android.contracts.FormManagementSectionInteractor;
import org.odk.collect.android.preferences.GeneralSharedPreferences;
import org.odk.collect.android.utilities.LocaleHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

import static org.odk.collect.android.preferences.GeneralKeys.KEY_APP_LANGUAGE;


/**
 * The {@link Application} class for the app. This extends {@link Application} because the app module has a dependency on
 * the odk-collect library. Also, since the app module expresses a dependency on the commons module, the {@link Application}
 * class for app module must implement the {@link MainApplication}.
 *
 * @author Pranav Sharma
 * @see MainApplication
 */
public class MyApplication extends Application implements MainApplication, LifecycleObserver, LoggableApplication {

    protected ApplicationComponent applicationComponent;

    private Activity currentActivity = null;
    private RxBus eventBus = null;
    private EventBus rxEventBus = null;
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    public static FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static boolean isOnline = true;

    /**
     * All the external modules must be initialised here. This includes any modules that have an init
     * function in their drivers. Also, any application level subscribers for the event bus,
     * in this case {@link RxBus} must be defined here.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        eventBus = new RxBus();
        initialiseLoggingComponent();
        Collect.getInstance().init(this, getApplicationContext(), new FormManagmentModuleInitialisationListener() {
            @Override
            public void onSuccess() {
                Grove.d("Form Module has been initialised correctly");
            }

            @Override
            public void onFailure(String message) {
                Grove.d("Form Module could not be initialised correctly");
                AlertDialogUtils.createErrorDialog(getApplicationContext(), "Could not start app as Form Module couldn't be initialised properly.", true);
            }
        }, this);
        setupRemoteConfig();
        setupActivityLifecycleListeners();
        InternetMonitor.init(this);
        initializeFormManagementPackage();
        AppNotificationUtils.createNotificationChannel(this);
        Grove.d("Initialising Ancillary Screens Module >>>>");
        AncillaryScreensDriver.init(this, AppConstants.BASE_API_URL,
                AppConstants.SEND_OTP_URL,
                AppConstants.UPDATE_PASSWORD_URL,
                getApplicationId(), getApplicationContext().getResources().getString(R.string.fusionauth_api_key));
        Grove.d("Ancillary Screens Module initialised >>>>");
        initBus();
        UpdateDriver.init(this);
        rxEventBus = new EventBus();
    }

    private void initialiseLoggingComponent() {
        LoggingComponentManager.registerGroveLoggingComponent(new GroveLoggingComponentLauncher());
        IGroveLoggingComponent initializer = LoggingComponentManager.iGroveLoggingComponent;
        if (initializer != null) {
            initializer.initializeLoggingComponent(this, this, getApplicationContext(), (context, s, s1, s2, s3) -> {
            }, true, true, BuildConfig.dsn, AppConstants.SENDER_EMAIL_ID, AppConstants.RECEIVER_EMAIL_ID);
        }

        new UCHandler.Builder(getCurrentApplication())
                .setTrackActivitiesEnabled(true)
                .setBackgroundModeEnabled(true)
                .build();
    }

    private void initializeFormManagementPackage() {
        Grove.d("Initialising Form Management Module >>>>");
        ComponentManager.registerFormManagementPackage(new FormManagementSectionInteractor());
        FormManagementCommunicator.setContract(ComponentManager.iFormManagementContract);
        ComponentManager.iFormManagementContract.setODKModuleStyle(this, R.drawable.login_bg, R.style.BaseAppTheme,
                R.style.FormEntryActivityTheme, R.style.BaseAppTheme_SettingsTheme_Dark, Long.MAX_VALUE);
        Grove.d("Form Management Module initialised >>>>");
    }

    public static String getApplicationId() {
        return AppConstants.APPLICATION_ID;
    }

    private void initBus() {
        compositeDisposable.add(this.getEventBus()
                .toObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(exchangeObject -> {
                    if (exchangeObject instanceof ExchangeObject) {
                        if (((ExchangeObject) exchangeObject).to == Modules.MAIN_APP
                                && ((ExchangeObject) exchangeObject).from == Modules.ANCILLARY_SCREENS
                                && isSignalExchangeType((ExchangeObject) exchangeObject)) {
                            ExchangeObject.SignalExchangeObject signalExchangeObject = (ExchangeObject.SignalExchangeObject) exchangeObject;
                            if (signalExchangeObject.shouldStartAsNewTask) {
                                Grove.d("Exchange event from %s intended to launch a new activity as a new task", currentActivity.getLocalClassName());
//                                signalExchangeObject.intentToLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(signalExchangeObject.intentToLaunch);
//                                currentActivity.finish();
                                if(currentActivity != null)
                                CommonUtilities.startActivityAsNewTask(signalExchangeObject.intentToLaunch, currentActivity);
                            } else
                                startActivity(signalExchangeObject.intentToLaunch);
                        } else if (exchangeObject instanceof ExchangeObject.EventExchangeObject) {
                            // TODO : Remove this just for test
                            ExchangeObject.EventExchangeObject eventExchangeObject = (ExchangeObject.EventExchangeObject) exchangeObject;
                            Grove.d("Event Received as Event exchange object %s ", eventExchangeObject.customEvents);
                            if (eventExchangeObject.to == Modules.MAIN_APP || eventExchangeObject.to == Modules.PROJECT) {
                                Grove.d("Event Received from Main App to Project Module is %s ", eventExchangeObject.customEvents);
                            }
                        } else if (exchangeObject instanceof ExchangeObject.NotificationExchangeObject) {
                            PendingIntent pendingIntent = ((ExchangeObject.NotificationExchangeObject) exchangeObject).data.getIntent();
                            int notificationID = ((ExchangeObject.NotificationExchangeObject) exchangeObject).data.getNotificationID();
                            int title = ((ExchangeObject.NotificationExchangeObject) exchangeObject).data.getTitle();
                            String body = ((ExchangeObject.NotificationExchangeObject) exchangeObject).data.getBody();
                            AppNotificationUtils.showNotification(getApplicationContext(), pendingIntent, notificationID, title, body);
                            Grove.d("Event Received for Push Notification consumption is %s ", title);
                        } else if (exchangeObject instanceof ExchangeObject.DataExchangeObject && (((ExchangeObject.DataExchangeObject) exchangeObject).to == Modules.MAIN_APP) &&
                                (((ExchangeObject.DataExchangeObject) exchangeObject).from == Modules.COMMONS) && (((ExchangeObject.DataExchangeObject) exchangeObject).data instanceof InternetStatus)) {
                            if (((ExchangeObject.DataExchangeObject) exchangeObject).data != null) {
                                InternetStatus internetStatus = (InternetStatus) ((ExchangeObject.DataExchangeObject) exchangeObject).data;
                                boolean status = internetStatus.isCurrentStatus();
                                updateInternetStatus(status);
                            }
                        } else if (!((((ExchangeObject) exchangeObject).type != ExchangeObject.ExchangeObjectTypes.SIGNAL)
                                && ((InternetStatus) ((ExchangeObject.DataExchangeObject) exchangeObject).data).isCurrentStatus())) {
                            try {
                                boolean status = ((InternetStatus) ((ExchangeObject.DataExchangeObject) exchangeObject).data).isCurrentStatus();
                                updateInternetStatus(status);
                            } catch (Exception e) {
                            }
                        } else {
                            Grove.e("Exchange Object received but not intended hence not mapped as per conditions, hence couldn't be consumed.");
                            Grove.e("Unconsumed exchange object values is %s", exchangeObject);
                        }
                    }
                }, Grove::e));
    }

    private boolean isSignalExchangeType(ExchangeObject exchangeObject) {
        return exchangeObject.type == ExchangeObject.ExchangeObjectTypes.SIGNAL;
    }

    @Override
    public void updateInternetStatus(Boolean status) {
        isOnline = status;
    }

    @Override
    public boolean isOnline() {
        return isOnline;
    }

    @Override
    public OkHttpClient provideOkHttpClient() {
        NetworkConnectionInterceptor networkConnectionInterceptor = OkHttpClientProvider.getInterceptor(this);
        return OkHttpClientProvider.provideOkHttpClient(networkConnectionInterceptor);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public Application getLoggableApplication() {
        return this;
    }

    /**
     * Must provide a {@link androidx.annotation.NonNull} activity instance of the activity running in foreground.
     * You can use {@link Application#registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks)} to
     * get the currently resumed activity (activity in foreground)
     */
    @Override
    public Activity getCurrentActivity() {
        return currentActivity;
    }

    /**
     * Must provide a {@link androidx.annotation.NonNull} instance of the current {@link Application}.
     */
    @Override
    public Application getCurrentApplication() {
        return this;
    }

    /**
     * Must provide a {@link androidx.annotation.NonNull} instance of {@link RxBus} which acts as an event bus
     * for the app.
     */
    @Override
    public RxBus getEventBus() {
        return bus();
    }

    /**
     * Optional method to teardown a module after its use is complete.
     * Not all modules require to be teared down.
     */
    @Override
    public void teardownModule(Modules module) {

    }

    @Override
    public EventBus eventBusInstance() {
        return rxEventBus;
    }

    private void setupActivityLifecycleListeners() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if(activity != null){
                    currentActivity = activity;
                    Grove.d("onCreate() called for Activity ... " + activity.getLocalClassName());
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if(activity != null){
                    currentActivity = activity;
                    Grove.d("onStart() called for Activity ... " + activity.getLocalClassName());
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = activity;
                if(activity != null){
                    Grove.d("onResume() called for Activity ... " + activity.getLocalClassName());
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Grove.d("onPause() called for Activity ... " + activity != null ? activity.getLocalClassName() : "No Activity Found");
                currentActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Grove.d("onStop() called for Activity ... " + activity != null ? activity.getLocalClassName() : "No Activity Founr");

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public void setupRemoteConfig() {
        FirebaseApp.initializeApp(this);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings).addOnCompleteListener(task -> mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Grove.d("Remote config activate successful. Config params updated :: %s", task1.getResult());
            } else {
                Grove.e("Remote config activation failed.");
            }
        }));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Collect.defaultSysLanguage = newConfig.locale.getLanguage();
        boolean isUsingSysLanguage = GeneralSharedPreferences.getInstance().get(KEY_APP_LANGUAGE).equals("");
        if (!isUsingSysLanguage) {
            Grove.d("Changing App language to: " + newConfig.locale.getLanguage());
            new LocaleHelper().updateLocale(this);
        }

    }

    public static FirebaseRemoteConfig getmFirebaseRemoteConfig() {
        return mFirebaseRemoteConfig;
    }


    private RxBus bus() {
        return eventBus;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onAppBackgrounded() {
        InternetMonitor.stopMonitoringInternet();
        if (compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
    }


    /**
     * Returns the Lifecycle of the provider.
     *
     * @return The lifecycle of the provider.
     */
    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return ProcessLifecycleOwner.get().getLifecycle();
    }

}
