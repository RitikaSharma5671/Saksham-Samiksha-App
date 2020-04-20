package com.samagra.user_profile.data.prefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.samagra.user_profile.data.network.model.LoginResponse;
import com.samagra.user_profile.di.ApplicationContext;
import com.samagra.user_profile.di.PreferenceInfo;

import javax.inject.Inject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Solid implementation of {@link CommonsPreferenceHelper}, performs the read/write operations on the {@link SharedPreferences}
 * used by the ancillaryscreens. The class is injected to all activities instead of manually creating an object.
 *
 * @author Pranav Sharma
 */
public class CommonsPrefsHelperImpl implements CommonsPreferenceHelper {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences defaultPreferences;
    Context context;

    @Inject
    public CommonsPrefsHelperImpl(@ApplicationContext Context context, @PreferenceInfo String prefFileName) {
        this.sharedPreferences = context.getSharedPreferences(prefFileName, MODE_PRIVATE);
        this.context = context;
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public String getCurrentUserName() {
        return defaultPreferences.getString("user.fullName", "");
    }

    @Override
    public String getCurrentUserId() {
        return defaultPreferences.getString("user.id", "");
    }

    @Override
    public String getPrefByKey(String param){
        return defaultPreferences.getString(param, "");
    }

    @Override
    public void setCurrentUserLoginFlags() {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putBoolean("justLoggedIn", true);

        boolean firstLogIn = sharedPreferences.getBoolean("firstLoginIn", false);
        if (firstLogIn) editor.putBoolean("firstLoginIn", false);
        else editor.putBoolean("firstLoginIn", true);

        boolean firstLogIn2 = sharedPreferences.getBoolean("firstLoginIn2", false);
        if (!firstLogIn2) editor.putBoolean("firstLoginIn2", true);
        else editor.putBoolean("firstLoginIn2", false);

        editor.apply();
    }

    @Override
    public boolean isFirstLogin() {
        return defaultPreferences.getBoolean("firstLoginIn", false);
    }

    @Override
    public boolean isFirstRun() {
        return defaultPreferences.getBoolean("", true);
    }

    @Override
    public void updateProfileKeyValuePair(String key, String value) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public String getProfileContentValueForKey(String key) {
        return defaultPreferences.getString(key, "");
    }

    @Override
    public boolean isShowSplash() {
        return defaultPreferences.getBoolean("", false);
    }

    @Override
    public Long getLastAppVersion() {
        return sharedPreferences.getLong("", 0);
    }

    @Override
    public void updateLastAppVersion(long updatedVersion) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putLong("", updatedVersion);
        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void updateFirstRunFlag(boolean value) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putBoolean("", false);
        editor.commit();
    }

    @Override
    public boolean isLoggedIn() {
        return defaultPreferences.getBoolean("isLoggedIn", false);
    }

    @Override
    public int getPreviousVersion() {
        return context.getSharedPreferences("VersionPref", MODE_PRIVATE).getInt("appVersionCode", 0);
    }

    @Override
    public void updateAppVersion(int currentVersion) {
        SharedPreferences.Editor editor = context.getSharedPreferences("VersionPref", MODE_PRIVATE).edit();
        editor.putInt("appVersionCode", currentVersion);
        editor.putBoolean("isAppJustUpdated", true);
        editor.commit();
    }

}
