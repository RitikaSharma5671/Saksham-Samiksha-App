package com.psx.odktest.di.modules;

import com.psx.odktest.di.PerActivity;
import com.psx.odktest.ui.HomeScreen.HomeInteractor;
import com.psx.odktest.ui.HomeScreen.HomeMvpInteractor;
import com.psx.odktest.ui.HomeScreen.HomeMvpPresenter;
import com.psx.odktest.ui.HomeScreen.HomeMvpView;
import com.psx.odktest.ui.HomeScreen.HomePresenter;

import dagger.Binds;
import dagger.Module;

/**
 * This module is similar to previous ones, it just uses Binds instead of Provides for better performance
 * Using Binds generates a lesser number of files during build times.
 */
@Module
public abstract class ActivityAbstractProviders {
    @Binds
    @PerActivity
    abstract HomeMvpPresenter<HomeMvpView, HomeMvpInteractor> provideHomeMvpPresenter(HomePresenter<HomeMvpView, HomeMvpInteractor> presenter);

    @Binds
    @PerActivity
    abstract HomeMvpInteractor provideHomeMvpInteractor(HomeInteractor homeInteractor);
}
