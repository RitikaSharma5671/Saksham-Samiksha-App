package com.psx.odktest.ui.VisitsScreen;

import com.psx.odktest.base.BasePresenter;

import javax.inject.Inject;

public class MyVisitsPresenter<V extends MyVisitsMvpView, I extends MyVisitMvpInteractor>
        extends BasePresenter<V, I> implements MyVisitsMvpPresenter<V, I> {

    @Inject
    public MyVisitsPresenter(I mvpInteractor) {
        super(mvpInteractor);
    }
}
