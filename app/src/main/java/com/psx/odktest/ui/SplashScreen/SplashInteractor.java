package com.psx.odktest.ui.SplashScreen;

import android.content.pm.PackageInfo;

import com.psx.odktest.base.BaseInteractor;
import com.psx.odktest.data.prefs.PreferenceHelper;

import org.odk.collect.android.preferences.GeneralKeys;
import org.odk.collect.android.preferences.GeneralSharedPreferences;

import javax.inject.Inject;

import static org.odk.collect.android.preferences.GeneralKeys.KEY_SPLASH_PATH;

public class SplashInteractor extends BaseInteractor implements SplashContract.Interactor {

    @Inject
    public SplashInteractor(PreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }

    @Override
    public boolean isFirstRun() {
        return getPreferenceHelper().isFirstRun();
    }

    @Override
    public boolean isShowSplash() {
        return getPreferenceHelper().isShowSplash();
    }

    /**
     * This function updates the version number and sets firstRun flag to true.
     * Call this method if you have for some reason updated the version code of the app.
     *
     * @param packageInfo - {@link PackageInfo} to get the the current version code of the app.
     * @return boolean - {@code true} if current package version code is higher than the stored version code
     * (indicating an app update), {@code false} otherwise
     */
    @Override
    public boolean updateVersionNumber(PackageInfo packageInfo) {
        if (getPreferenceHelper().getLastAppVersion() < packageInfo.versionCode) {
            getPreferenceHelper().updateLastAppVersion(packageInfo.versionCode);
            return true;
        }
        return false;
    }

    /**
     * Updates the first Run flag according to the conditions.
     *
     * @param value - the updated value of the first run flag
     */
    @Override
    public void updateFirstRunFlag(boolean value) {
        getPreferenceHelper().updateFirstRunFlag(value);
    }

    @Override
    public String getSplashPath() {
        return (String) GeneralSharedPreferences.getInstance().get(KEY_SPLASH_PATH);
    }
}
