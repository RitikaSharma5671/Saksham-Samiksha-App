package com.psx.ancillaryscreens.di.modules;

import com.psx.ancillaryscreens.di.PerActivity;
import com.psx.ancillaryscreens.screens.about.AboutContract;
import com.psx.ancillaryscreens.screens.about.AboutInteractor;
import com.psx.ancillaryscreens.screens.about.AboutPresenter;
import com.psx.ancillaryscreens.screens.login.LoginContract;
import com.psx.ancillaryscreens.screens.login.LoginInteractor;
import com.psx.ancillaryscreens.screens.login.LoginPresenter;
import com.psx.ancillaryscreens.screens.splash.SplashContract;
import com.psx.ancillaryscreens.screens.splash.SplashInteractor;
import com.psx.ancillaryscreens.screens.splash.SplashPresenter;

import dagger.Binds;
import dagger.Module;

/**
 * This module is similar to previous ones, it just uses Binds instead of Provides for better performance
 * Using Binds generates a lesser number of files during build times.
 */
@Module
public abstract class CommonsActivityAbstractProviders {

    @Binds
    @PerActivity
    abstract LoginContract.Presenter<LoginContract.View, LoginContract.Interactor> provideLoginMvpPresenter(
            LoginPresenter<LoginContract.View, LoginContract.Interactor> presenter);

    @Binds
    @PerActivity
    abstract LoginContract.Interactor provideLoginMvpInteractor(LoginInteractor interactor);

    @Binds
    @PerActivity
    abstract SplashContract.Presenter<SplashContract.View, SplashContract.Interactor> provideSplashMvpPresenter(
            SplashPresenter<SplashContract.View, SplashContract.Interactor> presenter);

    @Binds
    @PerActivity
    abstract SplashContract.Interactor provideSplashMvpInteractor(SplashInteractor splashInteractor);

    @Binds
    @PerActivity
    abstract AboutContract.Presenter<AboutContract.View, AboutContract.Interactor> provideAboutMvpPresenter(
            AboutPresenter<AboutContract.View, AboutContract.Interactor> presenter);

    @Binds
    @PerActivity
    abstract AboutContract.Interactor provideAboutMvpInteractor(AboutInteractor aboutInteractor);

}
