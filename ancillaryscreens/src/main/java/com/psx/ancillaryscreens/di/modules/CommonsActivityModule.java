package com.psx.ancillaryscreens.di.modules;

import android.app.Activity;
import android.content.Context;

import com.psx.ancillaryscreens.di.ActivityContext;
import com.psx.ancillaryscreens.di.ApplicationContext;
import com.psx.ancillaryscreens.di.PreferenceInfo;
import com.psx.commons.Constants;
import com.psx.ancillaryscreens.data.network.BackendCallHelper;
import com.psx.ancillaryscreens.data.network.BackendCallHelperImpl;
import com.psx.ancillaryscreens.data.prefs.CommonsPreferenceHelper;
import com.psx.ancillaryscreens.data.prefs.CommonsPrefsHelperImpl;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Classes marked with @{@link Module} are responsible for providing objects that can be injected.
 * Such classes define methods annotated with @{@link Provides}. The returned objects from such methods are
 * available for DI.
 *
 * @author Pranav Sharma
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
