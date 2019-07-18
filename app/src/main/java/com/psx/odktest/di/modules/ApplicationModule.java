package com.psx.odktest.di.modules;

import android.content.Context;

import com.psx.commons.MainApplication;
import com.psx.odktest.di.ApplicationContext;

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
}
