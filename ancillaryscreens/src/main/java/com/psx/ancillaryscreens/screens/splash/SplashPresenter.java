package com.psx.ancillaryscreens.screens.splash;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.psx.ancillaryscreens.AncillaryScreensDriver;
import com.psx.ancillaryscreens.base.BasePresenter;
import com.psx.ancillaryscreens.data.network.BackendCallHelper;
import com.psx.commons.Constants;
import com.psx.commons.ExchangeObject;
import com.psx.commons.Modules;

import org.odk.collect.android.BackgroundRxCalls.RxEvents;
import org.odk.collect.android.BackgroundRxCalls.UnzipDataTask;
import org.odk.collect.android.BackgroundRxCalls.WebCalls;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.PermissionListener;
import org.odk.collect.android.utilities.DialogUtils;
import org.odk.collect.android.utilities.PermissionUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * The presenter for the Splash Screen. This class controls the interactions between the View and the data.
 * Must implement {@link com.psx.ancillaryscreens.screens.splash.SplashContract.Presenter}
 *
 * @author Pranav Sharma
 */
public class SplashPresenter<V extends SplashContract.View, I extends SplashContract.Interactor> extends BasePresenter<V, I> implements SplashContract.Presenter<V, I> {

    private static final boolean EXIT = true;

    @Inject
    public SplashPresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable) {
        super(mvpInteractor, apiHelper, compositeDisposable);
    }


    @Override
    public void startUnzipTask() {
        UnzipDataTask unzipDataTask = new UnzipDataTask(getMvpView().getActivityContext());
        unzipDataTask.gunzipIt();
    }

    // Currently this function is not used.
    @Override
    public void startGetFormListCall() {
        WebCalls.GetFormsListCall(getMvpView().getActivityContext(),
                "http://142.93.208.135:8080/shiksha-saathi/get-formlist-for-role", new RxEvents() {
                    @Override
                    public void onComplete() {
                        boolean firstRun = getMvpInteractor().isFirstRun();
                        if (true/*sharedPreferences.getBoolean("isLoggedIn", false)*/) {
                            // TODO: Implement Login logic and perform asctions based on that.
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("On Error: Could not update the form list %s", e.getMessage());
                    }
                });
    }

    /**
     * Request the storage permissions which is necessary for ODK to read write data related to forms
     */
    @Override
    public void requestStoragePermissions() {
        PermissionUtils permissionUtils = new PermissionUtils();
        if (!PermissionUtils.areStoragePermissionsGranted(getMvpView().getActivityContext())) {
            permissionUtils.requestStoragePermissions((SplashActivity) getMvpView().getActivityContext(), new PermissionListener() {
                @Override
                public void granted() {
                    try {
                        Collect.createODKDirs();
                    } catch (RuntimeException e) {
                        DialogUtils.showDialog(DialogUtils.createErrorDialog((SplashActivity) getMvpView().getActivityContext(),
                                e.getMessage(), EXIT), (SplashActivity) getMvpView().getActivityContext());
                        return;
                    }
                    init();
                }

                @Override
                public void denied() {
                    getMvpView().finishActivity();
                }
            });
        } else {
            init();
        }
    }

    /**
     * Decides the next screen and moves to the decided screen.
     * This decision is based on the Login status which is managed by the {@link com.psx.ancillaryscreens.screens.login.LoginActivity}
     * in this module.
     *
     * @see com.psx.ancillaryscreens.screens.login.LoginActivity
     * @see com.psx.ancillaryscreens.data.prefs.CommonsPrefsHelperImpl
     */
    @Override
    public void moveToNextScreen() {
        if (getMvpInteractor().isLoggedIn()) {
            Timber.e("Moving to Home");
            Intent intent = new Intent(Constants.INTENT_LAUNCH_HOME_ACTIVITY);
            ExchangeObject.SignalExchangeObject signalExchangeObject = new ExchangeObject.SignalExchangeObject(Modules.MAIN_APP, Modules.ANCILLARY_SCREENS, intent, true);
            AncillaryScreensDriver.mainApplication.getEventBus().send(signalExchangeObject);
        } else {
            Timber.e("Launching Login");
            AncillaryScreensDriver.launchLoginScreen(getMvpView().getActivityContext());
        }
    }

    /**
     * This function initialises the {@link SplashActivity} by setting up the layout and updating necessary flags in
     * the {@link android.content.SharedPreferences}.
     */
    private void init() {
        getMvpView().showActivityLayout();
        PackageInfo packageInfo = null;
        try {
            packageInfo = getMvpView().getActivityContext().getPackageManager()
                    .getPackageInfo(getMvpView().getActivityContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "Unable to get package info");
        }

        boolean firstRun = getMvpInteractor().isFirstRun();
        boolean showSplash = getMvpInteractor().isShowSplash();
        String splashPath = getMvpInteractor().getSplashPath();

        // if you've increased version code, then update the version number and set firstRun to true
        boolean appUpdated = getMvpInteractor().updateVersionNumber(packageInfo);
        if (appUpdated)
            firstRun = true;

        if (firstRun || showSplash)
            getMvpInteractor().updateFirstRunFlag(false);
        getMvpView().showSimpleSplash();
    }
}
