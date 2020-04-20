package com.samagra.ancillaryscreens.screens.splash;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.BuildConfig;
import com.samagra.ancillaryscreens.base.BasePresenter;
import com.samagra.ancillaryscreens.data.network.BackendCallHelper;
import com.samagra.commons.Constants;
import com.samagra.commons.ExchangeObject;
import com.samagra.commons.Modules;
import com.samagra.commons.utils.AlertDialogUtils;
import com.samagra.commons.utils.AppPermissionUserActionListener;
import com.samagra.commons.utils.PermissionsHelper;
import com.samagra.commons.utils.UnzipRemoteData;


import org.odk.collect.android.contracts.IFormManagementContract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * The presenter for the Splash Screen. This class controls the interactions between the View and the data.
 * Must implement {@link com.samagra.ancillaryscreens.screens.splash.SplashContract.Presenter}
 *
 * @author Pranav Sharma
 */
public class SplashPresenter<V extends SplashContract.View, I extends SplashContract.Interactor> extends BasePresenter<V, I> implements SplashContract.Presenter<V, I> {

    private static final boolean EXIT = true;

    @Inject
    public SplashPresenter(I mvpInteractor, BackendCallHelper apiHelper,
                           CompositeDisposable compositeDisposable, IFormManagementContract iFormManagementContract) {
        super(mvpInteractor, apiHelper, compositeDisposable, iFormManagementContract);
    }


    @Override
    public void startUnzipTask() {
//        UnzipRemoteData unzipDataTask = new UnzipDataTask(getMvpView().getActivityContext());
//        unzipDataTask.gunzipIt();
    }

    // Currently this function is not used.
    @Override
    public void startGetFormListCall() {
//        WebCalls.GetFormsListCall(getMvpView().getActivityContext(),
//                "http://142.93.208.135:8080/shiksha-saathi/get-formlist-for-role", new RxEvents() {
//                    @Override
//                    public void onComplete() {
//                        boolean firstRun = getMvpInteractor().isFirstRun();
//                        if (true/*sharedPreferences.getBoolean("isLoggedIn", false)*/) {
//                            // TODO: Implement Login logic and perform asctions based on that.
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Timber.e("On Error: Could not update the form list %s", e.getMessage());
//                    }
//                });
//    }
    }

    @Override
    public void requestStoragePermissions() {
        PermissionsHelper permissionUtils = new PermissionsHelper();
        if (!PermissionsHelper.areStoragePermissionsGranted(getMvpView().getActivityContext())) {
            permissionUtils.requestStoragePermissions((SplashActivity) getMvpView().getActivityContext(), new AppPermissionUserActionListener() {
                @Override
                public void granted() {
                    try {
                        getIFormManagementContract().createODKDirectories();
                    } catch (RuntimeException e) {
                        AlertDialogUtils.showDialog(AlertDialogUtils.createErrorDialog((SplashActivity) getMvpView().getActivityContext(),
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
     * This decision is based on the Login status which is managed by the {@link com.samagra.ancillaryscreens.screens.login.LoginActivity}
     * in this module.
     *
     * @see com.samagra.ancillaryscreens.screens.login.LoginActivity
     * @see com.samagra.ancillaryscreens.data.prefs.CommonsPrefsHelperImpl
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
    @Override
    public void init() {
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
        updateCurrentVersion();
        getIFormManagementContract().resetEverythingODK();
    }


    static void resetEverything(){
//
//        final List<Integer> resetActions = new ArrayList<>();
//        resetActions.add(ResetUtility.ResetAction.RESET_FORMS);
//        resetActions.add(ResetUtility.ResetAction.RESET_PREFERENCES);
//        resetActions.add(ResetUtility.ResetAction.RESET_LAYERS);
//        resetActions.add(ResetUtility.ResetAction.RESET_CACHE);
//        resetActions.add(ResetUtility.ResetAction.RESET_OSM_DROID);
//
//        List<Integer> failedResetActions = new ResetUtility().reset(Collect.getInstance().getApplicationContext(), resetActions);
//        Timber.e("Reset Complete" + failedResetActions.size());
//
//        File dir = new File(Collect.INSTANCES_PATH);
//        if (dir.isDirectory()) {
//            String[] children = dir.list();
//            for (int i = 0; i < children.length; i++) {
//                new File(dir, children[i]).delete();
//            }
//        }
    }

    static void resetPreviousForms(){

//        final List<Integer> resetActions = new ArrayList<>();
//        resetActions.add(ResetUtility.ResetAction.RESET_FORMS);
//        resetActions.add(ResetUtility.ResetAction.RESET_PREFERENCES);
//        resetActions.add(ResetUtility.ResetAction.RESET_LAYERS);
//        resetActions.add(ResetUtility.ResetAction.RESET_CACHE);
//        resetActions.add(ResetUtility.ResetAction.RESET_OSM_DROID);
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                List<Integer> failedResetActions = new ResetUtility().reset(Collect.getInstance().getApplicationContext(), resetActions);
//                Timber.e("Reset Complete");
//            }
//        };
//        new Thread(runnable).start();
    }

    private void updateCurrentVersion(){
        int currentVersion = BuildConfig.VERSION_CODE;
        int previousSavedVersion = getMvpInteractor().getPreferenceHelper().getPreviousVersion();
        if(previousSavedVersion < currentVersion){
            resetEverything();
            getMvpInteractor().getPreferenceHelper().updateAppVersion(currentVersion);
            Timber.e("Up version detected");
        }
    }
}
