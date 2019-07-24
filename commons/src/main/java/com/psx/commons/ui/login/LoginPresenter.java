package com.psx.commons.ui.login;

import com.psx.commons.base.BasePresenter;

import javax.inject.Inject;

public class LoginPresenter<V extends LoginContract.View, I extends LoginContract.Interactor> extends BasePresenter<V,I> implements LoginContract.Presenter<V,I> {

    @Inject
    public LoginPresenter(I mvpInteractor) {
        super(mvpInteractor);
    }
}
