package com.psx.ancillaryscreens.base;

import com.psx.ancillaryscreens.data.network.BackendCallHelper;

import io.reactivex.disposables.CompositeDisposable;

public interface MvpPresenter<V extends MvpView, I extends MvpInteractor> {
    V getMvpView();

    I getMvpInteractor();

    BackendCallHelper getApiHelper();

    CompositeDisposable getCompositeDisposable();

    void onAttach(V mvpView);

    void onDetach();

    boolean isViewAttached();
}
