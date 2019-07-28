package com.psx.odktest.ui.SplashScreen;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.psx.odktest.base.BasePresenter;

import org.odk.collect.android.BackgroundRxCalls.RxEvents;
import org.odk.collect.android.BackgroundRxCalls.UnzipDataTask;
import org.odk.collect.android.BackgroundRxCalls.WebCalls;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.PermissionListener;
import org.odk.collect.android.utilities.DialogUtils;
import org.odk.collect.android.utilities.PermissionUtils;

import javax.inject.Inject;

import timber.log.Timber;

public class SplashPresenter<V extends SplashContract.View, I extends SplashContract.Interactor> extends BasePresenter<V, I> implements SplashContract.Presenter<V, I> {

    private static final boolean EXIT = true;

    @Inject
    public SplashPresenter(I mvpInteractor) {
        super(mvpInteractor);
    }

    @Override
    public void startUnzipTask() {
        UnzipDataTask unzipDataTask = new UnzipDataTask(getMvpView().getActivityContext());
        unzipDataTask.gunzipIt();
    }

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

    @Override
    public void requestStoragePermissions() {
        new PermissionUtils().requestStoragePermissions((SplashActivity) getMvpView().getActivityContext(), new PermissionListener() {
            @Override
            public void granted() {
                // must be at the beginning of any activity that can be called from an external intent
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
                // The activity has to finish because ODK Collect cannot function without these permissions.
                if (getMvpView() != null)
                    getMvpView().finishActivity();
            }
        });
    }

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
