package com.psx.ancillaryscreens.data.prefs;

import com.psx.ancillaryscreens.data.network.model.LoginResponse;

/**
 * Interface defining the access point to the SharedPreference used by the ancillaryscreens module.
 * All access functions to be implemented by a single solid implementation of this interface.
 *
 * @author Pranav Sharma
 * @see CommonsPrefsHelperImpl
 */
public interface CommonsPreferenceHelper {
    String getCurrentUserName();

    String getCurrentUserId();

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

    void updateProfileKeyValuePair(String key, String value);

    String getProfileContentValueForKey(String key);
}
