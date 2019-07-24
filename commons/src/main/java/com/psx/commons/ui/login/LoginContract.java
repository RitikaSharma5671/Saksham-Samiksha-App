package com.psx.commons.ui.login;

import android.widget.EditText;

import com.psx.commons.base.MvpInteractor;
import com.psx.commons.base.MvpPresenter;
import com.psx.commons.base.MvpView;
import com.psx.commons.data.network.model.LoginRequest;

public interface LoginContract {
    interface View extends MvpView {

        /**
         * Validates the login credentials inputted by the user and returns appropriate messages in case of failed login attempt.
         *
         * @param editTextUsername - The {@link EditText} in which user is supposed to type in the username.
         * @param editTextPassword - The {@link EditText} in which user is supposed to type in the password.
         * @return a boolean indicating the result of validation.
         */
        boolean validateInputs (EditText editTextUsername, EditText editTextPassword);

        void callHelpline();

        void performLogin();

        void changePassword();

        void onLoginSuccess();

        void onLoginFailed();
    }

    interface Interactor extends MvpInteractor {

    }



    interface Presenter<V extends View, I extends Interactor> extends MvpPresenter<V, I> {

        void startAuthenticationTask(LoginRequest loginRequest);
    }
}
