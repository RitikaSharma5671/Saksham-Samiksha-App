package com.psx.odktest.ui.SplashScreen;

import android.content.pm.PackageInfo;

import com.psx.odktest.base.MvpInteractor;
import com.psx.odktest.base.MvpPresenter;
import com.psx.odktest.base.MvpView;

public interface SplashContract {
    interface View extends MvpView {
        void endSplashScreen();

        void showSimpleSplash();

        void finishActivity();

        void showActivityLayout();
    }

    interface Interactor extends MvpInteractor {
        boolean isFirstRun();

        boolean isShowSplash();

        /**
         * This function updates the version number and sets firstRun flag to true.
         * Call this method if you have for some reason updated the version code of the app.
         *
         * @param packageInfo - {@link PackageInfo} to get the the current version code of the app.
         * @return boolean - {@code true} if current package version code is higher than the stored version code
         * (indicating an app update), {@code false} otherwise
         */
        boolean updateVersionNumber(PackageInfo packageInfo);

        /**
         * Updates the first Run flag according to the conditions.
         *
         * @param value - the updated value of the first run flag
         */
        void updateFirstRunFlag(boolean value);

        String getSplashPath();

        boolean isLoggedIn();
    }

    interface Presenter<V extends View, I extends Interactor> extends MvpPresenter<V, I> {
        void startUnzipTask();

        void startGetFormListCall();

        void requestStoragePermissions();

        void moveToNextScreen();
    }
}
