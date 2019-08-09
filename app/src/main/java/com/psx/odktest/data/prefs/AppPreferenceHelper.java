package com.psx.odktest.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.psx.odktest.di.ApplicationContext;
import com.psx.odktest.di.PreferenceInfo;

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

}
