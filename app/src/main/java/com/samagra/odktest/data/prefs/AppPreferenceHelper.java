package com.samagra.odktest.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.samagra.odktest.di.ApplicationContext;
import com.samagra.odktest.di.PreferenceInfo;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    @Inject
    public AppPreferenceHelper(@ApplicationContext Context context, @PreferenceInfo String prefFileName) {
        this.sharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public String getCurrentUserName() {
        return defaultPreferences.getString("user.fullName", "");
    }

    @Override
    public String getUserRoleFromPref() {
        try {
            String userDataJsonStr = sharedPreferences.getString("user.data", "");
            Map roleData = (Map) new Gson().fromJson(userDataJsonStr, HashMap.class).get("roleData");
            return new HashMap<String, String>(roleData).get("designation").toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void updateFormVersion(String version) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("formVersion", version);
        editor.apply();
    }

    @Override
    public String getUdiseListVersion() {
        return sharedPreferences.getString("udiseListVersion", "0");
    }

    @Override
    public String getFormVersion() {
        return sharedPreferences.getString("formVersion", "0");
    }


}
