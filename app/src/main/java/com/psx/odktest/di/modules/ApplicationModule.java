package com.psx.odktest.di.modules;

import android.content.Context;

import com.psx.commons.MainApplication;
import com.psx.commons.data.network.BackendCallHelper;
import com.psx.commons.data.network.BackendCallHelperImpl;
import com.psx.odktest.AppConstants;
import com.psx.odktest.data.prefs.AppPreferenceHelper;
import com.psx.odktest.data.prefs.PreferenceHelper;
import com.psx.odktest.di.ApplicationContext;
import com.psx.odktest.di.PreferenceInfo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final MainApplication mainApplication;

    public ApplicationModule(MainApplication application) {
        this.mainApplication = application;
    }

    @ApplicationContext
    @Provides
    Context provideContext() {
        return mainApplication.getCurrentApplication().getApplicationContext();
    }

    @Provides
    MainApplication provideApplication() {
        return mainApplication;
    }

    @Singleton
    @Provides
    PreferenceHelper providePreferenceHelper(AppPreferenceHelper appPreferenceHelper) {
        return appPreferenceHelper;
    }

    @Provides
    @PreferenceInfo
    String providePreferenceFileName() {
        return AppConstants.PREF_FILE_NAME;
    }

}
