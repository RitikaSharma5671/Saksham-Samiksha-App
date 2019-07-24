package com.psx.commons.base;

public interface MvpPresenter<V extends MvpView, I extends MvpInteractor> {
    V getMvpView();

    I getMvpInteractor();

    void onAttach(V mvpView);

    void onDetach();

    boolean isViewAttached();
}
