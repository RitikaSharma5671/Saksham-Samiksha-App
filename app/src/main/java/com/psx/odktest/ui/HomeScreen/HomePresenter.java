package com.psx.odktest.ui.HomeScreen;

import android.view.View;

import com.psx.odktest.base.BasePresenter;

public class HomePresenter<V extends HomeMvpView, I extends HomeMvpInteractor> extends BasePresenter<V, I> implements HomeMvpPresenter<V, I> {

    public HomePresenter(I mvpInteractor) {
        super(mvpInteractor);
    }

    @Override
    public void onMyVisitClicked(View v) {

    }

    @Override
    public void onInspectSchoolClicked(View v) {

    }

    @Override
    public void onSubmitFormClicked(View v) {

    }

    @Override
    public void onViewIssuesClicked(View v) {

    }

    @Override
    public void helplineClicked(View v) {

    }

    @Override
    public String getWelcomeText() {
        return null;
    }

    @Override
    public void downloadForms(String formName, String formID) {

    }
}
