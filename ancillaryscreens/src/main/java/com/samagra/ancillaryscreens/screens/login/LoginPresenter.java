package com.samagra.ancillaryscreens.screens.login;

import android.content.Context;
import android.content.Intent;

import com.androidnetworking.error.ANError;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.base.BasePresenter;
import com.samagra.ancillaryscreens.data.network.BackendCallHelper;
import com.samagra.ancillaryscreens.data.network.BackendCallHelperImpl;
import com.samagra.ancillaryscreens.data.network.model.LoginRequest;
import com.samagra.commons.Constants;
import com.samagra.commons.ExchangeObject;
import com.samagra.commons.Modules;
import com.samagra.grove.logging.Grove;

import org.odk.collect.android.contracts.IFormManagementContract;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * The presenter for the Login Screen. This class controls the interactions between the View and the data.
 * Must implement {@link com.samagra.ancillaryscreens.screens.login.LoginContract.Presenter}
 *
 * @author Pranav Sharma
 */
public class LoginPresenter<V extends LoginContract.View, I extends LoginContract.Interactor> extends BasePresenter<V, I> implements LoginContract.Presenter<V, I> {

    @Inject
    public LoginPresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable, IFormManagementContract iFormManagementContract) {
        super(mvpInteractor, apiHelper, compositeDisposable, iFormManagementContract);
    }

    /**
     * This function starts the Login process by accepting a {@link LoginRequest} and then executing it.
     *
     * @param loginRequest - The {@link LoginRequest} passed to make the API call via {@link BackendCallHelperImpl#performLoginApiCall(LoginRequest)}
     * @param activityContext
     * @see BackendCallHelperImpl#performLoginApiCall(LoginRequest)
     */
    @Override
    public void startAuthenticationTask(LoginRequest loginRequest, Context activityContext) {
        getCompositeDisposable().add(getApiHelper()
                .performLoginApiCall(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                         if (loginResponse.token != null) {
                            Grove.d("Received successful login response for the user");
                             ((LoginActivity)activityContext).onLoginSuccess(loginResponse);
                            getMvpInteractor().persistUserData(loginResponse);
                        } else
                             ((LoginActivity)activityContext).onLoginFailed(activityContext.getResources().getString(R.string.errorlogin));

                }, throwable -> {
                    if (throwable != null && throwable instanceof ANError) {
                        if(((ANError) (throwable)) != null && ((ANError) (throwable)).getErrorCode() == 404) {
                            ((LoginActivity)activityContext).onLoginFailed(activityContext.getResources().getString(R.string.incorrect_credentials));
                        }else {
                            ((LoginActivity)activityContext).onLoginFailed(activityContext.getResources().getString(R.string.errorlogin));
                        }
                    }else{
                        ((LoginActivity)activityContext).onLoginFailed(activityContext.getResources().getString(R.string.errorlogin));
                    }

                    Grove.e("Login error " + throwable);
                }));
    }

    /**
     * This function finishes the {@link LoginActivity} and starts the HomeActivity. The HomeActivity is outside this
     * module and can be any activity which has {@link com.samagra.commons.Constants#INTENT_LAUNCH_HOME_ACTIVITY} defined
     * as action in its intent-filter tag in AndroidManifest. This activity is started as a new task.
     * A {@link com.samagra.commons.ExchangeObject.SignalExchangeObject} is used to notify the launch of such activity.
     */
    @Override
    public void finishAndMoveToHomeScreen() {

    }


}
