package com.psx.ancillaryscreens.screens.login;

import android.content.Intent;

import com.psx.ancillaryscreens.AncillaryScreensDriver;
import com.psx.ancillaryscreens.base.BasePresenter;
import com.psx.ancillaryscreens.data.network.BackendCallHelper;
import com.psx.ancillaryscreens.data.network.model.LoginRequest;
import com.psx.ancillaryscreens.data.network.model.LoginResponse;
import com.psx.commons.Constants;
import com.psx.commons.ExchangeObject;
import com.psx.commons.Modules;

import org.odk.collect.android.utilities.ResetUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
                    if (LoginPresenter.this.getMvpView() != null) {
                        if (loginResponse.token != null) {
                            Timber.d("Response is %s ", loginResponse.toString());
                            LoginPresenter.this.getMvpView().onLoginSuccess(loginResponse);
                        } else
                            LoginPresenter.this.getMvpView().onLoginFailed();
                    }
                }, throwable -> {
                    LoginPresenter.this.getMvpView().onLoginFailed();
                    Timber.e(throwable);
                }));
    }

    @Override
    public void resetSelectedIfRequired() {
        if (getMvpInteractor().isFirstLogin()) {
            resetSelected();
        }
    }

    @Override
    public void finishAndMoveToHomeScreen() {
        Intent intent = new Intent(Constants.INTENT_LAUNCH_HOME_ACTIVITY);
        ExchangeObject.SignalExchangeObject signalExchangeObject = new ExchangeObject.SignalExchangeObject(ExchangeObject.ExchangeObjectTypes.SIGNAL, Modules.MAIN_APP, Modules.ANCILLARY_SCREENS, intent, true);
        AncillaryScreensDriver.mainApplication.getEventBus().send(signalExchangeObject);
        getMvpView().finishActivity();
    }

    private void resetSelected() {
        final List<Integer> resetActions = new ArrayList<>();
        resetActions.add(ResetUtility.ResetAction.RESET_FORMS);
        if (!resetActions.isEmpty()) {
            Runnable runnable = () -> new ResetUtility().reset(getMvpView().getActivityContext(), resetActions);
            new Thread(runnable).start();
        }
    }

}
