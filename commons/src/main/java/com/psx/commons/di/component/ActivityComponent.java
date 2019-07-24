package com.psx.commons.di.component;

import com.psx.commons.di.PerActivity;
import com.psx.commons.di.modules.CommonsActivityAbstractProviders;
import com.psx.commons.di.modules.CommonsActivityModule;
import com.psx.commons.ui.login.LoginActivity;


import javax.inject.Inject;

import dagger.Component;

/**
 * A @{@link Component} annotated interface defines connection between provider of objects (@{@link dagger.Module}
 * and the objects which express a dependency. It is implemented internally by Dagger at build time.
 */
@PerActivity
@Component(modules = {CommonsActivityModule.class, CommonsActivityAbstractProviders.class})
public interface ActivityComponent {

    void inject(LoginActivity loginActivity);

}
