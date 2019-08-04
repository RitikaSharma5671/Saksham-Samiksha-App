package com.psx.odktest.ui.VisitsScreen;

import android.view.View;

import com.psx.odktest.base.MvpPresenter;
import com.psx.odktest.di.PerActivity;

/**
 * The Presenter 'contract' for the HomeScreen. The {@link MyVisitsPresenter} <b>must</b> implement this interface.
 * This interface exposes presenter methods to the view ({@link MyVisitsActivity}) so that the business logic is defined
 * in the presenter, but can be called from the view.
 * This interface should be a type of {@link MvpPresenter}
 *
 * @author Pranav Sharma
 */
@PerActivity
public interface MyVisitsMvpPresenter<V extends MyVisitsMvpView, I extends MyVisitMvpInteractor> extends MvpPresenter<V, I> {

    void onViewVisitStatusClicked(View v);

    void onViewSubmittedFormsClicked(View v);
}
