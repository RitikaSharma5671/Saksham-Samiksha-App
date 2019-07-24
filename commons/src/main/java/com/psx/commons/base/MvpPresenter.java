package com.psx.commons.base;

import com.psx.commons.data.network.BackendCallHelper;

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
