package com.samagra.odktest.di.modules;

import android.graphics.SumPathEffect;

import com.samagra.ancillaryscreens.screens.login.LoginContract;
import com.samagra.ancillaryscreens.screens.login.LoginInteractor;
import com.samagra.ancillaryscreens.screens.login.LoginPresenter;
import com.samagra.odktest.di.PerActivity;
import com.samagra.odktest.ui.HomeScreen.HomeInteractor;
import com.samagra.odktest.ui.HomeScreen.HomeMvpInteractor;
import com.samagra.odktest.ui.HomeScreen.HomeMvpPresenter;
import com.samagra.odktest.ui.HomeScreen.HomeMvpView;
import com.samagra.odktest.ui.HomeScreen.HomePresenter;
import com.samagra.odktest.ui.SearchActivity.SearchInteractor;
import com.samagra.odktest.ui.SearchActivity.SearchMvpInteractor;
import com.samagra.odktest.ui.SearchActivity.SearchMvpPresenter;
import com.samagra.odktest.ui.SearchActivity.SearchMvpView;
import com.samagra.odktest.ui.SearchActivity.SearchPresenter;
import com.samagra.odktest.ui.Submissions.SubmissionsInteractor;
import com.samagra.odktest.ui.Submissions.SubmissionsMvpInteractor;
import com.samagra.odktest.ui.Submissions.SubmissionsMvpPresenter;
import com.samagra.odktest.ui.Submissions.SubmissionsMvpView;
import com.samagra.odktest.ui.Submissions.SubmissionsPresenter;
import com.samagra.odktest.ui.VisitsScreen.MyVisitMvpInteractor;
import com.samagra.odktest.ui.VisitsScreen.MyVisitsInteractor;
import com.samagra.odktest.ui.VisitsScreen.MyVisitsMvpPresenter;
import com.samagra.odktest.ui.VisitsScreen.MyVisitsMvpView;
import com.samagra.odktest.ui.VisitsScreen.MyVisitsPresenter;

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
    abstract SubmissionsMvpPresenter<SubmissionsMvpView, SubmissionsMvpInteractor> provideSubmissionsMvpPresenter(
            SubmissionsPresenter<SubmissionsMvpView, SubmissionsMvpInteractor> presenter);

    @Binds
    @PerActivity
    abstract SubmissionsMvpInteractor provideSubmissionsMvpInteractor(SubmissionsInteractor submissionsInteractor);


}
