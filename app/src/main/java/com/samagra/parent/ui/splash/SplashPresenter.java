package com.samagra.parent.ui.splash;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.androidnetworking.error.ANError;
import com.samagra.commons.MainApplication;
import com.samagra.commons.ScreenChangeEvent;
import com.samagra.commons.firebase.FirebaseUtilitiesWrapper;
import com.samagra.commons.firebase.IFirebaseRemoteStorageFileDownloader;
import com.samagra.commons.utils.AlertDialogUtils;
import com.samagra.commons.utils.FileUnzipper;
import com.samagra.commons.utils.UnzipTaskListener;
import com.samagra.grove.logging.Grove;
import com.samagra.parent.R;
import com.samagra.parent.base.BasePresenter;
import com.samagra.parent.helper.BackendNwHelper;

import org.odk.collect.android.BuildConfig;
import org.odk.collect.android.activities.SplashScreenActivity;
import org.odk.collect.android.application.Collect1;
import org.odk.collect.android.contracts.AppPermissionUserActionListener;
import org.odk.collect.android.contracts.IFormManagementContract;
import org.odk.collect.android.contracts.PermissionsHelper;
import org.odk.collect.android.listeners.PermissionListener;
import org.odk.collect.android.storage.StorageInitializer;
import org.odk.collect.android.utilities.DialogUtils;
import org.odk.collect.android.utilities.PermissionUtils;
import org.odk.collect.android.utilities.ThemeUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * The presenter for the Splash Screen. This class controls the interactions between the View and the data.
 * Must implement {@link SplashContract.Presenter}
 *
 * @author Pranav Sharma
 */
@SuppressWarnings("deprecation")
public class SplashPresenter<V extends SplashContract.View, I extends SplashContract.Interactor> extends BasePresenter<V, I> implements SplashContract.Presenter<V, I> {

    private static final String ROOT = Collect1.getInstance().getStoragePathProvider().getScopedStorageRootDirPath();
    private static final boolean EXIT = true;

    public boolean isJwtTokenValid() {
        return jwtTokenValid;
    }

    private boolean jwtTokenValid = false;
    private MainApplication mainApplication;


    @Inject
    public SplashPresenter(I mvpInteractor, CompositeDisposable compositeDisposable, BackendNwHelper backendNwHelper, IFormManagementContract iFormManagementContract) {
        super(mvpInteractor, compositeDisposable, backendNwHelper, iFormManagementContract);
    }

    public void initialise(MainApplication applicationContext) {
        mainApplication = applicationContext;
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
    }

    public boolean isNetworkConnected() {
        if (getMvpView() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getMvpView()
                    .getActivityContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return true;
    }

    @Override
    public void verifyJWTTokenValidity(String apiKey, Context activityContext) {
        if (getMvpInteractor().isLoggedIn() && !getMvpInteractor().getRefreshToken().equals("") && isNetworkConnected()) {
            String jwtToken = getMvpInteractor().getPreferenceHelper().getToken();
            getCompositeDisposable().add(getApiHelper()
                    .validateToken(jwtToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(updatedToken -> {
                        if (updatedToken != null && updatedToken.has("jwt")) {
                            jwtTokenValid = true;
                            ((SplashActivity) activityContext).endSplashScreen();
                            Grove.e("JWT Token found to be valid for this user with value: " + updatedToken.toString());
                        }

                    }, throwable -> {
                        jwtTokenValid = false;
                        Grove.d("JWT Token network call failed for this user, trying to update the JWT Token");
                        updateJWT(apiKey);
                        Grove.e(throwable);
                    }));
        } else {
            ((SplashActivity) activityContext).launchLoginScreen();
        }
    }


    /**
     * This function initialises the {@link SplashActivity} by setting up the layout and updating necessary flags in
     * the {@link android.content.SharedPreferences}.
     */
    private void init(String packageName, PackageManager packageManager, Context context) {
        startUnzipTask(context);
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Grove.e(e, "Unable to get package info");
        }

        boolean firstRun = getMvpInteractor().isFirstRun();
        boolean showSplash = getMvpInteractor().isShowSplash();

        // if you've increased version code, then update the version number and set firstRun to true
        boolean appUpdated = getMvpInteractor().updateVersionNumber(packageInfo);
        if (appUpdated)
            firstRun = true;

        if (firstRun || showSplash)
            getMvpInteractor().updateFirstRunFlag(false);
        ((SplashActivity) context).showSimpleSplash();
        updateCurrentVersion();
    }

    @Override
    public void requestStoragePermissions(String packageName, PackageManager packageManager, Context context) {
//        getIFormManagementContract().enableUsingScopedStorage();
        PermissionUtils permissionUtils = new PermissionUtils(new ThemeUtils(context).getMaterialDialogTheme());
//        if (!PermissionsHelper.areStoragePermissionsGranted(context)) {
//            permissionUtils.requestStoragePermissions((SplashActivity) context, new AppPermissionUserActionListener() {
//                @Override
//                public void granted() {
//                   vff(context);
//                    init(packageName, packageManager, context);
//                }
//
//                @Override
//                public void denied() {
//                    ((SplashActivity)context).finishSplashScreen();
//                }
//            });
//        } else {
//            init(packageName, packageManager, context);
//        }
        permissionUtils.requestStoragePermissions((SplashActivity) context, new PermissionListener() {
            @Override
            public void granted() {
                // must be at the beginning of any activity that can be called from an external intent
                try {
                    new StorageInitializer().createOdkDirsOnStorage();
                } catch (RuntimeException e) {
                    DialogUtils.showDialog(DialogUtils.createErrorDialog((SplashActivity) context,
                            e.getMessage(), EXIT), (SplashActivity) context);
                    return;
                }

//                vff(context);
                init(packageName, packageManager, context);
            }

            @Override
            public void denied() {
                // The activity has to finish because ODK Collect cannot function without these permissions.
                ((SplashActivity)context).finish();
            }
        });
    }

    public void vff(Context context) {
        try {
//            getIFormManagementContract().createODKDirectories();
            Timber.d("CREATED DIRECDDDDDDDDDDDDDDD");
            if (!getIFormManagementContract().isScopedStorageUsed())
                getIFormManagementContract().observeStorageMigration(context);

        } catch (RuntimeException e) {
            AlertDialogUtils.showDialog(AlertDialogUtils.createErrorDialog(context,
                    e.getMessage(), EXIT), (SplashActivity) context);
            return;
        }
    }

    @Override
    public boolean canLaunchHome() {
        return getMvpInteractor().isLoggedIn() && !getMvpInteractor().getRefreshToken().equals("");
    }


    @Override
    public void startUnzipTask(Context context) {
//        FileUnzipper fileUnzipper = new FileUnzipper(context, ROOT + "/saksham_data_json.json", R.raw.kist_school, new UnzipTaskListener() {
//            @Override
//            public void unZipSuccess() {
//                Grove.d("Data file has been unzipped successfully.");
////                IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
////                iStudentDetailsContract.loadSchoolDistrictData();
//            }
//
//            @Override
//            public void unZipFailure(Exception exception) {
//                Grove.e("Could not unzip the data file.");
//            }
//        });
//        fileUnzipper.unzipFile();
    }

    @Override
    public void downloadFirebaseRemoteStorageConfigFile() {
        FirebaseUtilitiesWrapper.downloadFile(ROOT + "/saksham_data_json.json.gzip", new IFirebaseRemoteStorageFileDownloader() {

            @Override
            public void onFirebaseRemoteStorageFileDownloadFailure(Exception exception) {
//                Grove.d("Remote file from Firebase failed with error. " + exception.getMessage() + " Using local file only, ");
//                startUnzipTask();
            }

            @Override
            public void onFirebaseRemoteStorageFileDownloadProgressState(long progressPercentage) {

            }

            @Override
            public void onFirebaseRemoteStorageFileDownloadSuccess() {

            }
        });
    }


    private void updateCurrentVersion() {
        int currentVersion = BuildConfig.VERSION_CODE;
        int previousSavedVersion = getMvpInteractor().getPreferenceHelper().getPreviousVersion();
        if (previousSavedVersion < currentVersion) {
            getMvpInteractor().getPreferenceHelper().updateAppVersion(currentVersion);
//            getIFormManagementContract().resetEverythingODK();
            Grove.e("Up version detected");
        }
    }

    @Override
    public void updateJWT(String apiKey) {
        boolean firstRun = getMvpInteractor().isFirstRun();
        if (!firstRun) {
            if (getMvpInteractor().isLoggedIn()) {
                String refreshToken = getMvpInteractor().getRefreshToken();
                Grove.e(refreshToken);
                getCompositeDisposable().add(getApiHelper()
                        .refreshToken(apiKey, refreshToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(updatedToken -> {
                            if (updatedToken != null && updatedToken.has("token")) {
                                getMvpInteractor().updateToken(updatedToken.getString("token"));
                                mainApplication.eventBusInstance().post(new ScreenChangeEvent("Splash", "Home"));
                            } else {
                                mainApplication.eventBusInstance().post(new ScreenChangeEvent("Splash", "Login"));
                            }
                        }, throwable -> {
                            if (throwable instanceof ANError) {
                                Grove.e("ANError Received while fetching JWT Token with error " + throwable);
                            } else {
                                Grove.e("Fetch JWT Failed... " + throwable);
                            }
                            mainApplication.eventBusInstance().post(new ScreenChangeEvent("Splash", "Login"));
                        }));
            }
        }
    }

    public void setInclompleteProfileCount() {
        getMvpInteractor().getPreferenceHelper().updateCountFlag(false);
    }
}


