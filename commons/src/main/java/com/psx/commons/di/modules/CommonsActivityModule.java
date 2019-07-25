package com.psx.commons.di.modules;

import android.app.Activity;
import android.content.Context;

import com.psx.commons.Constants;
import com.psx.commons.data.network.BackendCallHelper;
import com.psx.commons.data.network.BackendCallHelperImpl;
import com.psx.commons.data.prefs.CommonsPreferenceHelper;
import com.psx.commons.data.prefs.CommonsPrefsHelperImpl;
import com.psx.commons.di.ActivityContext;
import com.psx.commons.di.ApplicationContext;
import com.psx.commons.di.PreferenceInfo;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Classes marked with @{@link Module} are responsible for providing objects that can be injected.
 * Such classes define methods annotated with @{@link Provides}. The returned objects from such methods are
 * available for DI.
 */
@Module
public class CommonsActivityModule {

    private Activity activity;

    public CommonsActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return activity;
    }

    @Provides
    Activity provideActivity() {
        return activity;
    }

    @Provides
    @ApplicationContext
    Context provideApplicationContext() {
        return activity.getApplication().getApplicationContext();
    }

    @Provides
    CommonsPreferenceHelper provideCommonsPreferenceHelper(CommonsPrefsHelperImpl commonsPrefsHelper) {
        return commonsPrefsHelper;
    }

    @Provides
    @PreferenceInfo
    String providePreferenceFileName() {
        return Constants.COMMON_SHARED_PREFS_NAME;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    BackendCallHelper provideApiHelper() {
        return BackendCallHelperImpl.getInstance();
    }
}
