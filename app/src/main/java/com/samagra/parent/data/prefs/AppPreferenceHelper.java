package com.samagra.parent.data.prefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.samagra.commons.Constants;
import com.samagra.commons.InstitutionInfo;
import com.samagra.commons.LocaleManager;
import com.samagra.commons.PreferenceKeys;
import com.samagra.parent.di.ApplicationContext;
import com.samagra.parent.di.PreferenceInfo;

import org.odk.collect.android.preferences.GeneralKeys;
import org.odk.collect.android.utilities.LocaleHelper;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Solid implementation of the {@link PreferenceHelper}, performs the read/write operations on the {@link SharedPreferences}
 * used by the app module. The class is injected to all the activities instead of manually creating an object.
 *
 * @author Pranav Sharma
 */
@Singleton
public class AppPreferenceHelper implements PreferenceHelper {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences defaultPreferences;
    private final Context context;

    @Inject
    public AppPreferenceHelper(@ApplicationContext Context context, @PreferenceInfo String prefFileName) {
        this.sharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    @Override
    public String getCurrentUserName() {
        return defaultPreferences.getString("user.username", "");
    }

    @Override
    public String getCurrentUserFullName() {
        if (defaultPreferences.getString("user.fullName", "").equals(""))
            return defaultPreferences.getString("user.username", "");
        else
            return defaultPreferences.getString("user.fullName", "");

    }

    @Override
    public int fetchSchoolCode() {
        String schoolCode = defaultPreferences.getString("user.schoolCode", "");
        if (!schoolCode.isEmpty())
            return Integer.parseInt(schoolCode);
        else
            return 0;
    }

    @Override
    public boolean isTeacher() {
        String designation = defaultPreferences.getString("user.designation", "");
        return !designation.contains("DDO") &&(designation.contains("TGT") ||
                designation.contains("Clerk") ||
                designation.contains("Tabla") ||
                designation.contains("Vocation") ||
                designation.contains("Librarian") ||
                designation.contains("Computer")||
                designation.contains("Laboratory") ||
                designation.contains("Classical & Vernacular Teacher") ||
                designation.contains("PRT") ||
                designation.contains("JBT") ||
                designation.contains("PGT"));
    }

    @Override
    public boolean isSchool() {
        return defaultPreferences.getString("user.designation", "").equals("School Head");
    }

    @Override
    public boolean isUserSchoolHead() {
        String designation = defaultPreferences.getString("user.designation", "");
        return designation.contains("Head Master")
                || designation.contains("Head Master High School")
                || designation.contains("Head Teacher")
                || designation.equals("Principal")
        || designation.contains("DDO");
    }

    @Override
    public boolean isSchoolUpdated() {
        return defaultPreferences.getBoolean("schoolUpdated", false);
    }

    @Override
    public String fetchSchoolName() {
        return defaultPreferences.getString("user.schoolName", "");
    }

    @Override
    public boolean isProfileComplete() {
        String phoneNumber = "", userAccountName = "";
        if (defaultPreferences != null) {
            phoneNumber = defaultPreferences.getString("user.mobilePhone", "");
            userAccountName = defaultPreferences.getString("user.fullName", "");
        }
        if (phoneNumber != null) {
//            if (userAccountName != null) {
            return !phoneNumber.equals("");
//                        && !userAccountName.equals("");
//            }
        }
        return true;
    }

    @Override
    public boolean hasSeenDialog() {
        return defaultPreferences.getBoolean("isIncompleteDialogShown", false);

    }

    @Override
    public void updateCountFlag(boolean flag) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putBoolean("isIncompleteDialogShown", flag);
        editor.apply();
    }

    @Override
    public void prefillSchoolInfo() {
        String district = defaultPreferences.getString("user.district", "");
        String block = defaultPreferences.getString("user.block", "");
        String schoolName = defaultPreferences.getString("user.schoolName", "");
        String schoolCode = defaultPreferences.getString("user.schoolCode", "");
        InstitutionInfo selectedSchoolData = new InstitutionInfo(district, block, schoolName, Integer.parseInt(schoolCode));
        defaultPreferences.edit().putString("studentGeoData", generateObjectForStudentData(selectedSchoolData)).apply();
    }

    @Override
    public void downloadedStudentData(boolean flag) {
        defaultPreferences.edit().putBoolean("downloadedStudentData", flag).apply();
    }

    @Override
    public String getBlock() {
        return defaultPreferences.getString("user.block", "");
    }

    @Override
    public String getDistrict() {
        return defaultPreferences.getString("user.district", "");
    }

    @Override
    public boolean hasDownloadedStudentData() {
        return defaultPreferences.getBoolean("downloadedStudentData", false);
    }

    private String generateObjectForStudentData(InstitutionInfo inputObject) {
        try {
            Gson gson = new Gson();
            return gson.toJson(inputObject);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public int getPreviousVersion() {
        return context.getSharedPreferences("VersionPref", MODE_PRIVATE).getInt("appVersionCode", 0);
    }

    @Override
    public String getToken() {
        return defaultPreferences.getString("token", "");
    }


    @Override
    public boolean isFirstLogin() {
        return defaultPreferences.getBoolean("firstLoginIn", false);
    }

    @Override
    public boolean isFirstRun() {
        return defaultPreferences.getBoolean(PreferenceKeys.KEY_FIRST_RUN, true);
    }

    @Override
    public boolean isShowSplash() {
        return defaultPreferences.getBoolean(GeneralKeys.KEY_SHOW_SPLASH, false);
    }

    @Override
    public String getRefreshToken() {
        return defaultPreferences.getString("refreshToken", "");
    }

    @Override
    public void updateAppVersion(int currentVersion) {
        SharedPreferences.Editor editor = context.getSharedPreferences("VersionPref", MODE_PRIVATE).edit();
        editor.putInt("appVersionCode", currentVersion);
        editor.putBoolean("isAppJustUpdated", true);
        editor.commit();
    }

    @Override
    public void updateToken(String token) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    @Override
    public boolean isLoggedIn() {
        return defaultPreferences.getBoolean("isLoggedIn", false);
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void updateFirstRunFlag(boolean value) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putBoolean(GeneralKeys.KEY_FIRST_RUN, false);
        editor.commit();
    }

    @Override
    public Long getLastAppVersion() {
        return sharedPreferences.getLong(GeneralKeys.KEY_LAST_VERSION, 0);
    }

    @Override
    public void updateLastAppVersion(long updatedVersion) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putLong(GeneralKeys.KEY_LAST_VERSION, updatedVersion);
        editor.apply();
    }

    @Override
    public void updateFormVersion(String version) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("formVersion", version);
        editor.apply();
    }

    @Override
    public String getValueForKey(String content) {
        if (content.equals("user.mobilePhone")) {
            return new HashMap<String, String>((Map) new Gson().fromJson(defaultPreferences.getString("user.data", ""), HashMap.class)).get("phone");
        } else
            return defaultPreferences.getString(content, "");
    }

    @Override
    public String getCurrentUserId() {
        return defaultPreferences.getString("user.id", "");
    }

    @Override
    public String updateAppLanguage() {
        if (defaultPreferences.getString(Constants.APP_LANGUAGE_KEY, "en").equals("en")) {
            return "en";
        } else {
            return "hi";
        }
    }

    @Override
    public String getUserRoleFromPref() {
        return defaultPreferences.getString("user.designation", "");
    }


    @Override
    public String getFormVersion() {
        return sharedPreferences.getString("formVersion", "0");
    }

    @Override
    public String fetchCurrentSystemLanguage() {
        if (defaultPreferences.getString("currentLanguage", "").isEmpty()) {
            defaultPreferences.edit().putString("currentLanguage", LocaleManager.ENGLISH).apply();
            return LocaleManager.HINDI;
        } else {
            return defaultPreferences.getString("currentLanguage", "");
        }
    }

}
