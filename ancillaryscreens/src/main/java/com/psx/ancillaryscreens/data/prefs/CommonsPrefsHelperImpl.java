package com.psx.ancillaryscreens.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.psx.ancillaryscreens.data.network.model.LoginResponse;
import com.psx.ancillaryscreens.di.ApplicationContext;
import com.psx.ancillaryscreens.di.PreferenceInfo;

import javax.inject.Inject;

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
        if (response.getUser().has("accountName"))
            editor.putString("user.accountName", response.getUser().get("accountName").getAsString());
        else editor.putString("user.accountName", "");

        if (response.getUser().has("email"))
            editor.putString("user.email", response.getUser().get("email").getAsString());
        else editor.putString("user.email", "");

        if (response.getUser().has("username"))
            editor.putString("user.username", response.getUser().get("username").getAsString());
        else editor.putString("user.username", response.getUserName());

        if (response.getToken() != null)
            editor.putString("user.token", response.getToken().getAsString());

        if (response.getUser().has("data")) {
            editor.putString("user.data", response.getUser().get("data").toString());
            JsonObject data = response.getUser().get("data").getAsJsonObject();

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
        if (response.getUser().has("registrations")) {
            JsonArray registrations = response.getUser().get("registrations").getAsJsonArray();
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

        if (response.getUser().has("fullName"))
            editor.putString("user.fullName", response.getUser().get("fullName").getAsString());
        else editor.putString("user.fullName", response.getUser().get("username").getAsString());

        editor.putString("user.id", response.getUser().get("id").getAsString());

        if (response.getUser().has("mobilePhone"))
            editor.putString("user.mobilePhone", response.getUser().get("mobilePhone").getAsString());
        else editor.putString("user.mobilePhone", "");

        editor.putString("user.designation", response.getUser().get("data").getAsJsonObject().get("roleData").getAsJsonObject().get("designation").getAsJsonPrimitive().getAsString());
        editor.apply();
    }

    @Override
    public boolean isFirstLogin() {
        return defaultPreferences.getBoolean("firstLoginIn", false);
    }
}
