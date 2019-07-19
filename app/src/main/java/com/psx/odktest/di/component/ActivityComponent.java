package com.psx.odktest.di.component;

import com.psx.odktest.di.modules.ActivityAbstractProviders;
import com.psx.odktest.ui.HomeScreen.HomeActivity;
import com.psx.odktest.di.PerActivity;
import com.psx.odktest.di.modules.ActivityModule;
import com.psx.odktest.ui.VisitsScreen.MyVisitsActivity;

import dagger.Component;

/**
 * A @{@link Component} annotated interface defines connection between provider of objects (@{@link dagger.Module}
 * and the objects which express a dependency. It is implemented internally by Dagger at build time.
 */
@PerActivity
@Component(modules = {ActivityModule.class, ActivityAbstractProviders.class}, dependencies = {ApplicationComponent.class})
public interface ActivityComponent {

    void inject(HomeActivity homeActivity);

    void inject(MyVisitsActivity myVisitsActivity);
}
