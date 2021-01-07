package com.samagra.ancillaryscreens.screens.splash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.androidnetworking.error.ANError;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.BuildConfig;
import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.base.BasePresenter;
import com.samagra.ancillaryscreens.data.network.BackendCallHelper;
import com.samagra.commons.firebase.FirebaseUtilitiesWrapper;
import com.samagra.commons.firebase.IFirebaseRemoteStorageFileDownloader;
import com.samagra.commons.utils.AlertDialogUtils;
import com.samagra.commons.utils.FileUnzipper;
import com.samagra.commons.utils.UnzipTaskListener;
import com.samagra.grove.logging.Grove;

import org.odk.collect.android.contracts.AppPermissionUserActionListener;
import org.odk.collect.android.contracts.IFormManagementContract;
import org.odk.collect.android.contracts.PermissionsHelper;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * The presenter for the Splash Screen. This class controls the interactions between the View and the data.
 * Must implement {@link com.samagra.ancillaryscreens.screens.splash.SplashContract.Presenter}
 *
 * @author Pranav Sharma
 */
public class SplashPresenter<V extends SplashContract.View, I extends SplashContract.Interactor> extends BasePresenter<V, I> implements SplashContract.Presenter<V, I> {

    private static final String ROOT = Environment.getExternalStorageDirectory()
            + File.separator + "odk";
    private static final boolean EXIT = true;

    @Inject
    public SplashPresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable, IFormManagementContract iFormManagementContract) {
        super(mvpInteractor, apiHelper, compositeDisposable, iFormManagementContract);
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
//        if (getMvpInteractor().isLoggedIn() && !getMvpInteractor().getRefreshToken().equals("") ) {
        if ( !getMvpInteractor().getRefreshToken().equals("")  && getMvpInteractor().isLoggedIn()) {
            isJWTTokenValid();
        } else {
            launchLoginScreen();
        }
    }

    private void launchLoginScreen() {
        getIFormManagementContract().resetEverythingODK();
        Grove.d("Launching Login");
        AncillaryScreensDriver.launchLoginScreen(getMvpView().getActivityContext());
        getMvpView().finishActivity();
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

    private void isJWTTokenValid() {
        if (getMvpInteractor().isLoggedIn()) {
            if(isNetworkConnected()) {
                String jwtToken = getMvpInteractor().getPreferenceHelper().getToken();
                getCompositeDisposable().add(getApiHelper()
                        .validateToken(jwtToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(updatedToken -> {
                            if (updatedToken != null && updatedToken.has("user")) {
                                SplashPresenter.this.getIFormManagementContract().resetODKForms(SplashPresenter.this.getMvpView().getActivityContext());
                                getMvpView().redirectToHomeScreen();
                                Grove.e("Token found to be valid " + updatedToken.toString());
                            } else {
                                updateJWT();
                            }

                        }, throwable -> {
                            updateJWT();
                            Grove.e("Token found to be invalid "+ throwable);
                        }));
            } else {
                getMvpView().redirectToHomeScreen();
            }
        }
    }


    /**
     * This function initialises the {@link SplashActivity} by setting up the layout and updating necessary flags in
     * the {@link android.content.SharedPreferences}.
     */
    @Override
    public void init() {
        startUnzipTask();
        getMvpView().showActivityLayout();
        PackageInfo packageInfo = null;
        try {
            packageInfo = getMvpView().getActivityContext().getPackageManager()
                    .getPackageInfo(getMvpView().getActivityContext().getPackageName(), PackageManager.GET_META_DATA);
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
        getMvpView().showSimpleSplash();
        updateCurrentVersion();

    }

    public boolean checkAppSignature(Context context) {


        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager()

                    .getPackageInfo(context.getPackageName(),

                            PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                // SHA1 the signature
                String sha1 = getSHA1(signature.toByteArray());
                Log.d("vfrvtrf",  "vtgvgt >> " + sha1);
                // check is matches hardcoded value
                return APP_SIGNATURE.equals(sha1);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return false;
    }

    public static String getSHA1(byte[] sig) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.update(sig);
        byte[] hashtext = digest.digest();
        return bytesToHex(hashtext);
    }

    //util method to convert byte array to hex string
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static final String APP_SIGNATURE = "69D48BA03BF239993AE74C7C65CAA3C6D7E6EA28";

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


    @Override
    public void startUnzipTask() {
        FileUnzipper fileUnzipper = new FileUnzipper(getMvpView().getActivityContext(), ROOT + "/saksham_data_json.json", R.raw.saksham_data_json, new UnzipTaskListener() {
            @Override
            public void unZipSuccess() {
                Grove.d("Data file has been unzipped successfully.");
            }

            @Override
            public void unZipFailure(Exception exception) {
                Grove.e("Could not unzip the data file.");
            }
        });
        fileUnzipper.unzipFile();
//        getMvpView().renderLayoutVisible();
    }

    @Override
    public void downloadFirebaseRemoteStorageConfigFile() {
        FirebaseUtilitiesWrapper.downloadFile(ROOT + "/saksham_data_json.json.gzip", new IFirebaseRemoteStorageFileDownloader() {

            @Override
            public void onFirebaseRemoteStorageFileDownloadFailure(Exception exception) {
                Grove.d("Remote file from Firebase failed with error. " + exception.getMessage() + " Using local file only, ");
                startUnzipTask();
            }

            @Override
            public void onFirebaseRemoteStorageFileDownloadProgressState(long progressPercentage) {

            }

            @Override
            public void onFirebaseRemoteStorageFileDownloadSuccess() {
//                getMvpView().showSnackbar("Remote file from Firebase has been downloaded successfully", Snackbar.LENGTH_LONG);
                startUnzipTask();
            }
        });
    }



    private void updateCurrentVersion(){
        int currentVersion = BuildConfig.VERSION_CODE;
        int previousSavedVersion = getMvpInteractor().getPreferenceHelper().getPreviousVersion();
        if(previousSavedVersion < currentVersion){
            getMvpInteractor().getPreferenceHelper().updateAppVersion(currentVersion);
            getIFormManagementContract().resetEverythingODK();
            Grove.e("Up version detected");
        }
    }
    private void updateJWT() {
        boolean firstRun = getMvpInteractor().isFirstRun();
        if (!firstRun) {
            if (getMvpInteractor().isLoggedIn()) {
                String refreshToken = getMvpInteractor().getRefreshToken();
                String jwt = getMvpInteractor().getPreferenceHelper().getToken();
                Grove.e(refreshToken);
                getCompositeDisposable().add(getApiHelper()
                        .refreshToken(refreshToken, jwt)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(updatedToken -> {
                            if(updatedToken != null && updatedToken.has("statusCode")&&
                                    updatedToken.getInt("statusCode") == 200
                                    && updatedToken.has("successResponse")&& updatedToken.getJSONObject("successResponse").has("token")){
                                getMvpInteractor().updateToken(updatedToken.getJSONObject("successResponse").getString("token"));
                                getMvpView().redirectToHomeScreen();
                            }else{
                                launchLoginScreen();
                            }

                        }, throwable -> {
                            if (throwable instanceof ANError)
                                Grove.e("ERROR BODY %s ERROR CODE %s, ERROR DETAIL %s", ((ANError) (throwable)).getErrorBody(), ((ANError) (throwable)).getErrorCode(), ((ANError) (throwable)).getErrorDetail());
                            Grove.e(throwable);
                            launchLoginScreen();
                        }));
            }
        }
    }

}
