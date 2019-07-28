package com.psx.odktest.data.prefs;

import javax.inject.Singleton;

@Singleton
public interface PreferenceHelper {
    String getCurrentUserName();

    boolean isFirstRun();

    boolean isShowSplash();

    Long getLastAppVersion();

    void updateLastAppVersion(long updatedVersion);

    void updateFirstRunFlag(boolean value);
}