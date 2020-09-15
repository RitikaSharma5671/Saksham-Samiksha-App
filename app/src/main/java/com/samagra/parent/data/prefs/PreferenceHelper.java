package com.samagra.parent.data.prefs;

import javax.inject.Singleton;

/**
 * Interface defining the access point to {@link android.content.SharedPreferences} used by the app module.
 * All access functions to be implemented by a solid implementation of this interface. This implementation should be
 * a {@link Singleton}.
 *
 * @author Pranav Sharma
 * @see AppPreferenceHelper
 */
@Singleton
public interface PreferenceHelper {
    String getCurrentUserName();

    String getToken();

    boolean isFirstLogin();

    String getFormVersion();

    String fetchCurrentSystemLanguage();

    int getPreviousVersion();

    boolean isFirstRun();

    boolean isShowSplash();

    String getRefreshToken();

    void updateAppVersion(int currentVersion);

    void updateToken(String token);

    boolean isLoggedIn();

    void updateFirstRunFlag(boolean value);

    void updateLastAppVersion(long updatedVersion);

    Long getLastAppVersion();

    void updateFormVersion(String version);

    String getValueForKey(String content);

    String getCurrentUserId();

    String getUserRoleFromPref();

    String updateAppLanguage();

    String getCurrentUserFullName();

    int fetchSchoolCode();

    boolean isTeacher();

    boolean isSchool();

    boolean isSchoolUpdated();

    String fetchSchoolName();

    boolean isProfileComplete();

    boolean hasSeenDialog();

    void updateCountFlag(boolean flag);

    void prefillSchoolInfo();

    void downloadedStudentData(boolean flag);

    boolean hasDownloadedStudentData();
}