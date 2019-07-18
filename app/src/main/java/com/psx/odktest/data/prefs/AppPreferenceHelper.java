package com.psx.odktest.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.psx.odktest.di.ApplicationContext;
import com.psx.odktest.di.PreferenceInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppPreferenceHelper implements PreferenceHelper {

    private final SharedPreferences sharedPreferences;

    @Inject
    public AppPreferenceHelper(@ApplicationContext Context context, @PreferenceInfo String prefFileName) {
        this.sharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }
}
