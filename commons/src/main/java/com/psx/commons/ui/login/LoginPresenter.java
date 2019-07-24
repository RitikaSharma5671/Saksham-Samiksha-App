package com.psx.commons.ui.login;

import com.psx.commons.base.BasePresenter;
import com.psx.commons.data.network.BackendCallHelper;
import com.psx.commons.data.network.model.LoginRequest;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenter<V extends LoginContract.View, I extends LoginContract.Interactor> extends BasePresenter<V, I> implements LoginContract.Presenter<V, I> {

    @Inject
    public LoginPresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable) {
        super(mvpInteractor, apiHelper, compositeDisposable);
    }

    @Override
    public void startAuthenticationTask(LoginRequest loginRequest) {
        getCompositeDisposable().add(getApiHelper()
                .performLoginApiCall(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    if (getMvpView() != null) {
                        if (loginResponse.getResponse().isSuccessful())
                            getMvpView().onLoginSuccess();
                        else
                            getMvpView().onLoginFailed();
                    }
                }));
    }
}
