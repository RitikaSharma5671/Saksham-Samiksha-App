package com.psx.ancillaryscreens.data.prefs;

import com.psx.ancillaryscreens.data.network.model.LoginResponse;

public interface CommonsPreferenceHelper {
    String getCurrentUserName();

    void setCurrentUserLoginFlags();

    void setCurrentUserDetailsFromLogin(LoginResponse response);

    void setCurrentUserAdditionalDetailsFromLogin(LoginResponse response);

    boolean isFirstLogin();

    boolean isShowSplash();

    Long getLastAppVersion();

    void updateLastAppVersion(long updatedVersion);

    void updateFirstRunFlag(boolean value);

    boolean isLoggedIn();

    boolean isFirstRun();
}
