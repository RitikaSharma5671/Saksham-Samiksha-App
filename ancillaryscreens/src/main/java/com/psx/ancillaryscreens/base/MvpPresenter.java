package com.psx.ancillaryscreens.base;

import com.psx.ancillaryscreens.data.network.BackendCallHelper;

import io.reactivex.disposables.CompositeDisposable;

/**
 * This is the base interface that all the 'Presenter Contracts' must extend.
 * Methods maybe added to it as and when required.
 *
 * @author Pranav Sharma
 * @see com.psx.ancillaryscreens.screens.login.LoginContract.Presenter for example
 */
public interface MvpPresenter<V extends MvpView, I extends MvpInteractor> {
    V getMvpView();

    I getMvpInteractor();

    BackendCallHelper getApiHelper();

    CompositeDisposable getCompositeDisposable();

    void onAttach(V mvpView);

    void onDetach();

    boolean isViewAttached();
}
