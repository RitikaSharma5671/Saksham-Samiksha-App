package com.psx.ancillaryscreens.data.prefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.psx.ancillaryscreens.data.network.model.LoginResponse;
import com.psx.ancillaryscreens.di.ApplicationContext;
import com.psx.ancillaryscreens.di.PreferenceInfo;

import org.odk.collect.android.preferences.GeneralKeys;

import javax.inject.Inject;

/**
 * Solid implementation of {@link CommonsPreferenceHelper}, performs the read/write operations on the {@link SharedPreferences}
 * used by the ancillaryscreens. The class is injected to all activities instead of manually creating an object.
 *
 * @author Pranav Sharma
 */
public class CommonsPrefsHelperImpl implements CommonsPreferenceHelper {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences defaultPreferences;

    @Inject
    public CommonsPrefsHelperImpl(@ApplicationContext Context context, @PreferenceInfo String prefFileName) {
        this.sharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public String getCurrentUserName() {
        return defaultPreferences.getString("user.fullName", "");
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
    public void setCurrentUserDetailsFromLogin(LoginResponse response) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        if (response.user.has("accountName"))
            editor.putString("user.accountName", response.user.get("accountName").getAsString());
        else editor.putString("user.accountName", "");

        if (response.user.has("email"))
            editor.putString("user.email", response.user.get("email").getAsString());
        else editor.putString("user.email", "");

        if (response.user.has("username"))
            editor.putString("user.username", response.user.get("username").getAsString());
        else editor.putString("user.username", response.getUserName());

        if (response.token != null)
            editor.putString("user.token", response.token.getAsString());

        if (response.user.has("data")) {
            editor.putString("user.data", response.user.get("data").toString());
            JsonObject data = response.user.get("data").getAsJsonObject();

            if (data.has("category"))
                editor.putString("user.category", data.get("category").getAsJsonPrimitive().getAsString());
            else editor.putString("user.category", "");

            if (data.has("joiningDate"))
                editor.putString("user.joiningDate", data.get("joiningDate").getAsJsonPrimitive().getAsString());
            else editor.putString("user.joiningDate", "");
        }
        editor.apply();
    }

    @Override
    public void setCurrentUserAdditionalDetailsFromLogin(LoginResponse response) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        if (response.user.has("registrations")) {
            JsonArray registrations = response.user.get("registrations").getAsJsonArray();
            for (int i = 0; i < registrations.size(); i++) {
                if (registrations.get(i).getAsJsonObject().has("applicationId")) {
                    String applicationId = registrations.get(i).getAsJsonObject().get("applicationId").getAsString();
                    if (applicationId.equals("1ae074db-32f3-4714-a150-cc8a370eafd1")) {
                        // This is applicationId for Shiksha Saathi
                        editor.putString("user.role", registrations.get(i).getAsJsonObject().get("roles").getAsJsonArray().get(0).getAsJsonPrimitive().getAsString());
                        editor.putString("user.roleData", registrations.get(i).getAsJsonObject().get("roles").getAsJsonArray().toString());
                    }
                }
            }
        }

        if (response.user.has("fullName"))
            editor.putString("user.fullName", response.user.get("fullName").getAsString());
        else editor.putString("user.fullName", response.user.get("username").getAsString());

        editor.putString("user.id", response.user.get("id").getAsString());

        if (response.user.has("mobilePhone"))
            editor.putString("user.mobilePhone", response.user.get("mobilePhone").getAsString());
        else editor.putString("user.mobilePhone", "");

        JsonObject userData = response.user.get("data").getAsJsonObject();
        if (userData.has("roleData")) {
            if (userData.get("roleData").getAsJsonObject().has("designation"))
                editor.putString("user.designation", response.user.get("data").getAsJsonObject().get("roleData").getAsJsonObject().get("designation").getAsJsonPrimitive().getAsString());
        }
        editor.apply();
    }

    @Override
    public boolean isFirstLogin() {
        return defaultPreferences.getBoolean("firstLoginIn", false);
    }

    @Override
    public boolean isFirstRun() {
        return defaultPreferences.getBoolean(GeneralKeys.KEY_FIRST_RUN, true);
    }

    @Override
    public boolean isShowSplash() {
        return defaultPreferences.getBoolean(GeneralKeys.KEY_SHOW_SPLASH, false);
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

    @SuppressLint("ApplySharedPref")
    @Override
    public void updateFirstRunFlag(boolean value) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putBoolean(GeneralKeys.KEY_FIRST_RUN, false);
        editor.commit();
    }

    @Override
    public boolean isLoggedIn() {
        return defaultPreferences.getBoolean("isLoggedIn", false);
    }
}
