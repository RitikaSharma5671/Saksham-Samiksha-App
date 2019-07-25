package com.psx.ancillaryscreens.base;

import com.psx.ancillaryscreens.data.network.BackendCallHelper;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class BasePresenter<V extends MvpView, I extends MvpInteractor> implements MvpPresenter<V, I> {

    private V mvpView;
    private I mvpInteractor;
    private BackendCallHelper apiHelper;
    private CompositeDisposable compositeDisposable;

    @Inject
    public BasePresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable) {
        this.mvpInteractor = mvpInteractor;
        this.apiHelper = apiHelper;
        this.compositeDisposable = compositeDisposable;
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
    public BackendCallHelper getApiHelper() {
        return apiHelper;
    }

    @Override
    public CompositeDisposable getCompositeDisposable() {
        return this.compositeDisposable;
    }

    @Override
    public void onAttach(V mvpView) {
        this.mvpView = mvpView;
    }

    @Override
    public void onDetach() {
        this.mvpView = null;
        if (this.compositeDisposable != null)
            this.compositeDisposable.dispose();
    }

    @Override
    public boolean isViewAttached() {
        return this.mvpView != null;
    }
}
