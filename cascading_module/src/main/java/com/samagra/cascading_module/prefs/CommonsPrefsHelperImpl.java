package com.samagra.cascading_module.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.samagra.cascading_module.di.ApplicationContext;
import com.samagra.cascading_module.di.PreferenceInfo;

import java.util.HashMap;
import java.util.Map;

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
    public String getUserRoleFromPref() {
        return defaultPreferences.getString("user.designation", "");

    }

    @Override
    public String getUserName() {
        return defaultPreferences.getString("user.fullName", "");
    }

    @Override
    public String getCurrentUserFullName() {
        if (defaultPreferences.getString("user.fullName", "").equals(""))
            return defaultPreferences.getString("user.username", "");
        else
            return defaultPreferences.getString("user.fullName", "");

    }
}
