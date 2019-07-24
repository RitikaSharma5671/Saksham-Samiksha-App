package com.psx.odktest.base;

import javax.inject.Inject;

public class BasePresenter<V extends MvpView, I extends MvpInteractor> implements MvpPresenter<V, I> {

    private V mvpView;
    private I mvpInteractor;

    @Inject
    public BasePresenter(I mvpInteractor) {
        this.mvpInteractor = mvpInteractor;
    }

    @Override
    public V getMvpView() {
        return mvpView;
    }

    @Override
    public I getMvpInteractor() {
        return mvpInteractor;
    }

    @Override
    public void onAttach(V mvpView) {
        this.mvpView = mvpView;
    }

    @Override
    public void onDetach() {
        this.mvpView = null;
    }

    @Override
    public boolean isViewAttached() {
        return this.mvpView != null;
    }
}
