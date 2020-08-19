package com.samagra.parent.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.samagra.commons.Constants;
import com.samagra.commons.LocaleManager;
import com.samagra.parent.di.ApplicationContext;
import com.samagra.parent.di.PreferenceInfo;

import org.odk.collect.android.utilities.LocaleHelper;

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
        return defaultPreferences.getString("user.username", "");
    }

    @Override
    public String getCurrentUserFullName() {
        if(defaultPreferences.getString("user.fullName", "").equals(""))
            return defaultPreferences.getString("user.username", "");
        else
            return defaultPreferences.getString("user.fullName", "");

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
        try {
            return new HashMap<String, String>((Map) new Gson().fromJson(defaultPreferences.getString("user.data", ""), HashMap.class).get("roleData")).get("designation");
        } catch (Exception e) {
            return "";
        }
    }


    @Override
    public String getFormVersion() {
        return sharedPreferences.getString("formVersion", "0");
    }

    @Override
    public String fetchCurrentSystemLanguage() {
        if(defaultPreferences.getString("currentLanguage", "").isEmpty()) {
            defaultPreferences.edit().putString("currentLanguage", LocaleManager.ENGLISH).apply();
            return LocaleManager.HINDI;
        }else{
           return defaultPreferences.getString("currentLanguage", "");
        }
    }

}
