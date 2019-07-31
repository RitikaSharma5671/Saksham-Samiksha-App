package com.psx.odktest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.psx.ancillaryscreens.AncillaryScreensDriver;
import com.psx.commons.CommonUtilities;
import com.psx.commons.ExchangeObject;
import com.psx.commons.MainApplication;
import com.psx.commons.Modules;
import com.psx.commons.RxBus;
import com.psx.odktest.di.component.ApplicationComponent;
import com.psx.odktest.di.component.DaggerApplicationComponent;
import com.psx.odktest.di.modules.ApplicationModule;
import com.psx.odktest.ui.HomeScreen.HomeActivity;

import org.odk.collect.android.ODKDriver;
import org.odk.collect.android.application.Collect;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MyApplication extends Collect implements MainApplication, LifecycleObserver {

    protected ApplicationComponent applicationComponent;

    private Activity currentActivity = null;
    private RxBus eventBus = null;
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {
        super.onCreate();
        eventBus = new RxBus();
        setupActivityLifecycleListeners();
        AncillaryScreensDriver.init(this);
        ODKDriver.init(this, R.drawable.splash_screen_ss, R.style.BaseAppTheme, R.style.FormEntryActivityTheme, R.style.BaseAppTheme_SettingsTheme_Dark, Long.MAX_VALUE);
        compositeDisposable.add(this.getEventBus()
                .toObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(exchangeObject -> {
                    if (exchangeObject instanceof ExchangeObject) {
                        if (((ExchangeObject) exchangeObject).to == Modules.MAIN_APP
                                && ((ExchangeObject) exchangeObject).from == Modules.ANCILLARY_SCREENS
                                && ((ExchangeObject) exchangeObject).type == ExchangeObject.ExchangeObjectTypes.SIGNAL) {
                            ExchangeObject.SignalExchangeObject signalExchangeObject = (ExchangeObject.SignalExchangeObject) exchangeObject;
                            if (signalExchangeObject.shouldStartAsNewTask)
                                CommonUtilities.startActivityAsNewTask(signalExchangeObject.intentToLaunch, currentActivity);
                            else
                                startActivity(signalExchangeObject.intentToLaunch);
                        }
                    }
                }, Timber::e));
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
    public Activity getCurrentActivity() {
        return currentActivity;
    }

    @Override
    public Application getCurrentApplication() {
        return this;
    }

    @Override
    public RxBus getEventBus() {
        return bus();
    }

    @Override
    public void teardownModule(Modules module) {

    }

    private void setupActivityLifecycleListeners() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                currentActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private RxBus bus() {
        return eventBus;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onAppBackgrounded() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
    }
}
