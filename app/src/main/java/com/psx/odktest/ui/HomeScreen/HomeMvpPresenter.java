package com.psx.odktest.ui.HomeScreen;

import android.view.View;

import com.psx.odktest.base.MvpPresenter;
import com.psx.odktest.di.PerActivity;

@PerActivity
public interface HomeMvpPresenter<V extends HomeMvpView, I extends HomeMvpInteractor> extends MvpPresenter<V, I> {

    void onMyVisitClicked(View v);

    void onInspectSchoolClicked(View v);

    void onSubmitFormClicked(View v);

    void onViewIssuesClicked(View v);

    void helplineClicked(View v);

    String getWelcomeText();

    void downloadForms(String formName, String formID);
}
