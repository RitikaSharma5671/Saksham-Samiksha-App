package com.psx.ancillaryscreens.screens.login;

import android.content.Intent;

import com.androidnetworking.error.ANError;
import com.psx.ancillaryscreens.AncillaryScreensDriver;
import com.psx.ancillaryscreens.base.BasePresenter;
import com.psx.ancillaryscreens.data.network.BackendCallHelper;
import com.psx.ancillaryscreens.data.network.BackendCallHelperImpl;
import com.psx.ancillaryscreens.data.network.model.LoginRequest;
import com.psx.commons.Constants;
import com.psx.commons.ExchangeObject;
import com.psx.commons.Modules;

import org.odk.collect.android.utilities.ResetUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * The presenter for the Login Screen. This class controls the interactions between the View and the data.
 * Must implement {@link com.psx.ancillaryscreens.screens.login.LoginContract.Presenter}
 *
 * @author Pranav Sharma
 */
public class LoginPresenter<V extends LoginContract.View, I extends LoginContract.Interactor> extends BasePresenter<V, I> implements LoginContract.Presenter<V, I> {

    @Inject
    public LoginPresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable) {
        super(mvpInteractor, apiHelper, compositeDisposable);
    }

    /**
     * This function starts the Login process by accepting a {@link LoginRequest} and then executing it.
     *
     * @param loginRequest - The {@link LoginRequest} passed to make the API call via {@link BackendCallHelperImpl#performLoginApiCall(LoginRequest)}
     * @see BackendCallHelperImpl#performLoginApiCall(LoginRequest)
     */
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
                    if (throwable instanceof ANError)
                        Timber.e("ERROR BODY %s ERROR CODE %s, ERROR DETAIL %s", ((ANError) (throwable)).getErrorBody(), ((ANError) (throwable)).getErrorCode(), ((ANError) (throwable)).getErrorDetail());
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

    /**
     * This function finishes the {@link LoginActivity} and starts the HomeActivity. The HomeActivity is outside this
     * module and can be any activity which has {@link com.psx.commons.Constants#INTENT_LAUNCH_HOME_ACTIVITY} defined
     * as action in its intent-filter tag in AndroidManifest. This activity is started as a new task.
     * A {@link com.psx.commons.ExchangeObject.SignalExchangeObject} is used to notify the launch of such activity.
     */
    @Override
    public void finishAndMoveToHomeScreen() {
        Intent intent = new Intent(Constants.INTENT_LAUNCH_HOME_ACTIVITY);
        ExchangeObject.SignalExchangeObject signalExchangeObject = new ExchangeObject.SignalExchangeObject(Modules.MAIN_APP, Modules.ANCILLARY_SCREENS, intent, true);
        AncillaryScreensDriver.mainApplication.getEventBus().send(signalExchangeObject);
        getMvpView().finishActivity();
    }

    /**
     * Resets ODK form using {@link ResetUtility}. This action is required in some apps during the first time login.
     */
    private void resetSelected() {
        final List<Integer> resetActions = new ArrayList<>();
        resetActions.add(ResetUtility.ResetAction.RESET_FORMS);
        if (!resetActions.isEmpty()) {
            Runnable runnable = () -> new ResetUtility().reset(getMvpView().getActivityContext(), resetActions);
            new Thread(runnable).start();
        }
    }

}
