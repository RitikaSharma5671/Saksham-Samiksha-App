package com.psx.odktest.ui.SearchActivity;

import com.psx.odktest.base.BasePresenter;

import javax.inject.Inject;

public class SearchPresenter<V extends SearchMvpView, I extends SearchMvpInteractor> extends BasePresenter<V, I> implements SearchMvpPresenter<V, I> {

    @Inject
    public SearchPresenter(I mvpInteractor) {
        super(mvpInteractor);
    }
}
