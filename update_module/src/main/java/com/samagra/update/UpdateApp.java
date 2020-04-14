package com.samagra.update;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import static com.samagra.update.CheckForUpdate.FLEXIBLE;

//https://developer.android.com/guide/playcore/in-app-updates#immediate_flow

public class UpdateApp implements LifecycleObserver {

    private static final String TAG = "UpdateApp";
    int mCurrentAppVersion, mAvailableVersion;

    private WeakReference<AppCompatActivity> mActivityWeakReference;

    private static UpdateApp instance;

    // Default mode is FLEXIBLE
    private int mode = FLEXIBLE;

    // Creates instance of the manager.
    private AppUpdateManager appUpdateManager;

    // Returns an intent object that you use to check for an update.
    private Task<AppUpdateInfo> appUpdateInfoTask;

    public UpdateApp(AppCompatActivity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
        this.appUpdateManager = AppUpdateManagerFactory.create(getActivity());
        this.appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        activity.getLifecycle().addObserver(this);


        try {
            Application currentApplication = UpdateDriver.mainApplication.getCurrentApplication();
            PackageInfo pInfo = currentApplication.getPackageManager().getPackageInfo(getCurrentPackageName(), 0);
            mCurrentAppVersion = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        getAvailableVersionCode(new UpdateApp.onVersionCheckListener() {
            @Override
            public void onReceiveVersionCode(final int code) {

                mAvailableVersion = code;
                if (mCurrentAppVersion != mAvailableVersion) {
                    if (mAvailableVersion % 10 == 0)
                        mode(CheckForUpdate.IMMEDIATE).start();
                    else
                        mode(CheckForUpdate.FLEXIBLE).start();
                }
            }
        });


    }

    private String getCurrentPackageName() {
        return UpdateDriver.mainApplication.getCurrentApplication().getPackageName();
    }


    public static UpdateApp Builder(AppCompatActivity activity) {
        if (instance == null) {
            instance = new UpdateApp(activity);
        }
        Log.d(TAG, "Instance created");
        return instance;
    }

    public UpdateApp mode(int mode) {
        String strMode = mode == FLEXIBLE ? "FLEXIBLE" : "IMMEDIATE";
        Log.d(TAG, "Set update mode to : " + strMode);
        this.mode = mode;
        return this;
    }

    public void start() {
        if (mode == FLEXIBLE) {
            setUpListener();
        }
        checkUpdate();
    }

    private void checkUpdate() {
        // Checks that the platform will allow the specified type of update.
        Log.d(TAG, "Checking for updates");
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(mode)) {
                    // Request the update.
                    Log.d(TAG, "Update available");
                    startUpdate(appUpdateInfo);
                } else {
                    Log.d(TAG, "No Update available");
                }
            }
        });
    }

    private void startUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            Log.d(TAG, "Starting update");
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    mode,
                    getActivity(),
                    CheckForUpdate.REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            Log.d(TAG, "" + e.getMessage());
        }
    }

//    public static void handleResult(int requestCode, int resultCode){
//        Log.d("LIBRARY_ZMA", "Req code Update : " + requestCode);
//        if (requestCode == UpdateManagerConstant.REQUEST_CODE) {
//            Log.d("LIBRARY_ZMA", "Result code Update : " + resultCode);
//            if (resultCode != RESULT_OK) {
//                Log.d("LIBRARY_ZMA", "Update flow failed! Result code: " + resultCode);
//                // If the update is cancelled or fails,
//                // you can request to start the update again.
//            }
//        }
//    }

    private InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                Log.d(TAG, "An update has been downloaded");
                popupSnackbarForCompleteUpdate();
            }
        }
    };

    private void setUpListener() {
        appUpdateManager.registerListener(listener);
    }

    public void continueUpdate() {
        if (instance.mode == FLEXIBLE) {
            continueUpdateForFlexible();
        } else {
            continueUpdateForImmediate();
        }
    }

    private void continueUpdateForFlexible() {
        instance.appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                        // If the update is downloaded but not installed,
                        // notify the user to complete the update.
                        if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                            Log.d(TAG, "An update has been downloaded");
                            instance.popupSnackbarForCompleteUpdate();
                        }
                    }
                });
    }

    private void continueUpdateForImmediate() {
        instance.appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                        if (appUpdateInfo.updateAvailability()
                                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                            // If an in-app update is already running, resume the update.
                            try {
                                instance.appUpdateManager.startUpdateFlowForResult(
                                        appUpdateInfo,
                                        instance.mode,
                                        getActivity(),
                                        CheckForUpdate.REQUEST_CODE);
                            } catch (IntentSender.SendIntentException e) {
                                Log.d(TAG, "" + e.getMessage());
                            }
                        }
                    }
                });
    }

    private void popupSnackbarForCompleteUpdate() {
        @SuppressLint("WrongConstant") Snackbar snackbar =
                Snackbar.make(
                        getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    public void getAvailableVersionCode(final onVersionCheckListener onVersionCheckListener) {
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    // Request the update.
                    Log.d(TAG, "Update available");
                    int availableVersionCode = appUpdateInfo.availableVersionCode();
                    onVersionCheckListener.onReceiveVersionCode(availableVersionCode);
                } else {
                    Log.d(TAG, "No Update available");
                }
            }
        });
    }

    private Activity getActivity() {
        return mActivityWeakReference.get();
    }

    private void unregisterListener() {
        if (appUpdateManager != null && listener != null) {
            appUpdateManager.unregisterListener(listener);
            Log.d(TAG, "Unregistered the install state listener");
        }
    }

    public interface onVersionCheckListener {

        void onReceiveVersionCode(int code);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        unregisterListener();
    }
}