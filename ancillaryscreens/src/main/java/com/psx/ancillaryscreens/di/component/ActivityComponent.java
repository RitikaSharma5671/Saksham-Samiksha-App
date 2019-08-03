package com.psx.ancillaryscreens.di.component;

import com.psx.ancillaryscreens.di.PerActivity;
import com.psx.ancillaryscreens.di.modules.CommonsActivityAbstractProviders;
import com.psx.ancillaryscreens.di.modules.CommonsActivityModule;
import com.psx.ancillaryscreens.screens.about.AboutActivity;
import com.psx.ancillaryscreens.screens.login.LoginActivity;
import com.psx.ancillaryscreens.screens.splash.SplashActivity;

import dagger.Component;

/**
 * A {@link Component} annotated interface defines connection between provider of objects ({@link dagger.Module})
 * and the objects which express a dependency. It is implemented internally by Dagger at build time.
 * The modules mentioned in {@link Component} are the classes that are required to inject the activities mentioned
 * in this interface methods.
 *
 * @author Pranav Sharma
 */
@PerActivity
@Component(modules = {CommonsActivityModule.class, CommonsActivityAbstractProviders.class})
public interface ActivityComponent {

    void inject(LoginActivity loginActivity);

    void inject(SplashActivity splashActivity);

    void inject(AboutActivity aboutActivity);
}
