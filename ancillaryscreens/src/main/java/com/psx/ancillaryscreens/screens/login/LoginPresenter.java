package com.psx.ancillaryscreens.screens.login;

import android.content.Intent;

import com.psx.ancillaryscreens.base.BasePresenter;
import com.psx.ancillaryscreens.data.network.BackendCallHelper;
import com.psx.ancillaryscreens.data.network.model.LoginRequest;
import com.psx.ancillaryscreens.data.network.model.LoginResponse;

import org.odk.collect.android.utilities.ResetUtility;

import java.util.ArrayList;
import java.util.List;

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
                            getMvpView().onLoginSuccess(loginResponse);
                        else
                            getMvpView().onLoginFailed();
                    }
                }));
    }

    @Override
    public void resetSelectedIfRequired() {
        if (getMvpInteractor().isFirstLogin()) {
            resetSelected();
        }
    }

    @Override
    public void finishAndMoveToHomeScreen(LoginResponse loginResponse) {
        Intent toHomeActivity;
        /*if(BuildConfig.FLAVOR.equals("mSamvaad"))
            toHomeActivity = new Intent(this, HomeActivityMSamvaad.class);
        else toHomeActivity = new Intent(this, HomeActivity.class);

        try{
            toHomeActivity.putExtra("user.email", result.user.get("email").getAsString());
        }catch (Exception e){
            Log.e(TAG, "User email not found");
        }
        toHomeActivity.putExtra("user.id", result.user.get("id").getAsString());
        toHomeActivity.putExtra("token", result.token.getAsString());
        toHomeActivity.putExtra("user.username", result.user.get("username").getAsString());

        startActivity(toHomeActivity);
        finish();*/
        // TODO : Singnal event bus to start the HomeActivity
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
