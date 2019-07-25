package com.psx.ancillaryscreens.di.modules;

import com.psx.ancillaryscreens.di.PerActivity;
import com.psx.ancillaryscreens.screens.login.LoginContract;
import com.psx.ancillaryscreens.screens.login.LoginInteractor;
import com.psx.ancillaryscreens.screens.login.LoginPresenter;

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

}
