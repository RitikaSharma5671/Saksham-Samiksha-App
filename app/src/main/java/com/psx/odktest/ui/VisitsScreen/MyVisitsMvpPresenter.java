package com.psx.odktest.ui.VisitsScreen;

import android.view.View;

import com.psx.odktest.base.MvpPresenter;
import com.psx.odktest.di.PerActivity;

@PerActivity
public interface MyVisitsMvpPresenter<V extends MyVisitsMvpView, I extends MyVisitMvpInteractor> extends MvpPresenter<V, I> {

    void onViewVisitStatusClicked(View v);

    void onViewSubmittedFormsClicked(View v);
}
