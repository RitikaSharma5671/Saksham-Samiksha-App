package com.psx.odktest.di.modules;

import com.psx.odktest.di.PerActivity;
import com.psx.odktest.ui.HomeScreen.HomeInteractor;
import com.psx.odktest.ui.HomeScreen.HomeMvpInteractor;
import com.psx.odktest.ui.HomeScreen.HomeMvpPresenter;
import com.psx.odktest.ui.HomeScreen.HomeMvpView;
import com.psx.odktest.ui.HomeScreen.HomePresenter;
import com.psx.odktest.ui.SearchActivity.SearchInteractor;
import com.psx.odktest.ui.SearchActivity.SearchMvpInteractor;
import com.psx.odktest.ui.SearchActivity.SearchMvpPresenter;
import com.psx.odktest.ui.SearchActivity.SearchMvpView;
import com.psx.odktest.ui.SearchActivity.SearchPresenter;
import com.psx.odktest.ui.SplashScreen.SplashContract;
import com.psx.odktest.ui.SplashScreen.SplashInteractor;
import com.psx.odktest.ui.SplashScreen.SplashPresenter;
import com.psx.odktest.ui.VisitsScreen.MyVisitMvpInteractor;
import com.psx.odktest.ui.VisitsScreen.MyVisitsInteractor;
import com.psx.odktest.ui.VisitsScreen.MyVisitsMvpPresenter;
import com.psx.odktest.ui.VisitsScreen.MyVisitsMvpView;
import com.psx.odktest.ui.VisitsScreen.MyVisitsPresenter;

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
    abstract HomeMvpPresenter<HomeMvpView, HomeMvpInteractor> provideHomeMvpPresenter(
            HomePresenter<HomeMvpView, HomeMvpInteractor> presenter);

    @Binds
    @PerActivity
    abstract HomeMvpInteractor provideHomeMvpInteractor(HomeInteractor homeInteractor);

    @Binds
    @PerActivity
    abstract MyVisitsMvpPresenter<MyVisitsMvpView, MyVisitMvpInteractor> provideVisitsMvpPresenter(
            MyVisitsPresenter<MyVisitsMvpView, MyVisitMvpInteractor> presenter);

    @Binds
    @PerActivity
    abstract MyVisitMvpInteractor provideVistsMvpInteractor(MyVisitsInteractor myVisitsInteractor);

    @Binds
    @PerActivity
    abstract SearchMvpPresenter<SearchMvpView, SearchMvpInteractor> provideSearchMvpPresenter(
            SearchPresenter<SearchMvpView, SearchMvpInteractor> presenter);

    @Binds
    @PerActivity
    abstract SearchMvpInteractor provideSearchMvpInteractor(SearchInteractor searchInteractor);

    @Binds
    @PerActivity
    abstract SplashContract.Presenter<SplashContract.View, SplashContract.Interactor> provideSplashMvpPresenter(
            SplashPresenter<SplashContract.View, SplashContract.Interactor> presenter);

    @Binds
    @PerActivity
    abstract SplashContract.Interactor provideSplashMvpInteractor(SplashInteractor splashInteractor);
}
