package com.psx.ancillaryscreens.screens.login;

import android.widget.EditText;

import com.psx.ancillaryscreens.base.MvpInteractor;
import com.psx.ancillaryscreens.base.MvpPresenter;
import com.psx.ancillaryscreens.base.MvpView;
import com.psx.ancillaryscreens.data.network.model.LoginRequest;
import com.psx.ancillaryscreens.data.network.model.LoginResponse;

public interface LoginContract {
    interface View extends MvpView {

        /**
         * Validates the login credentials inputted by the user and returns appropriate messages in case of failed login attempt.
         *
         * @param editTextUsername - The {@link EditText} in which user is supposed to type in the username.
         * @param editTextPassword - The {@link EditText} in which user is supposed to type in the password.
         * @return a boolean indicating the result of validation.
         */
        boolean validateInputs(EditText editTextUsername, EditText editTextPassword);

        void callHelpline();

        void performLogin();

        void changePassword();

        void onLoginSuccess(LoginResponse loginResponse);

        void onLoginFailed();

        void showProgressDialog();

        void hideProgressDialog();

        void finishActivity();
    }

    interface Interactor extends MvpInteractor {

        void persistUserData(LoginResponse loginResponse);

        boolean isFirstLogin();
    }


    interface Presenter<V extends View, I extends Interactor> extends MvpPresenter<V, I> {

        void startAuthenticationTask(LoginRequest loginRequest);

        void resetSelectedIfRequired();

        void finishAndMoveToHomeScreen();
    }
}
