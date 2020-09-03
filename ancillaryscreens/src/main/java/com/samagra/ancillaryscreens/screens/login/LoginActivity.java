package com.samagra.ancillaryscreens.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;
import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.R2;
import com.samagra.ancillaryscreens.base.BaseActivity;
import com.samagra.ancillaryscreens.data.network.model.LoginRequest;
import com.samagra.ancillaryscreens.data.network.model.LoginResponse;
import com.samagra.ancillaryscreens.screens.change_password.ChangePasswordActivity;
import com.samagra.ancillaryscreens.screens.passReset.EnterMobileNumberFragment;
import com.samagra.ancillaryscreens.utils.SnackbarUtils;
import com.samagra.commons.CommonUtilities;
import com.samagra.grove.logging.Grove;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.sentry.Sentry;

/**
 * The View Part for the Login Screen, must implement {@link LoginContract.View}
 *
 * @author Pranav Sharma
 */
public class LoginActivity extends BaseActivity implements LoginContract.View {

    @BindView(R2.id.login_username)
    public AppCompatEditText editTextUsername;
    @BindView(R2.id.login_password)
    public AppCompatEditText editTextPassword;
    @BindView(R2.id.circularProgressBar)
    public ProgressBar progressBar;
    @BindView(android.R.id.content)
    public FrameLayout content;
    @BindView(R2.id.login_submit)
    public Button submitButton;
    @BindView(R2.id.userLayout)
    public TextInputLayout userLayout;
    @BindView(R2.id.pwdLayout)
    public TextInputLayout pwdLayout;
    @BindView(R2.id.loginParentlayout)
    public ConstraintLayout loginParentLayout;
    @BindView(R2.id.forgot_password)
    public TextView forgot_password;

    private Unbinder unbinder;

    @Inject
    LoginPresenter<LoginContract.View, LoginContract.Interactor> loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        getActivityComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        loginPresenter.onAttach(this);
        if(getIntent().getBooleanExtra("loggedOut", false)){
            SnackbarUtils.showShortSnackbar(loginParentLayout, getActivityContext().getResources().getString(R.string.successful_logout_message));
        }

        SpannableString content = new SpannableString(fetchString(R.string.forgot_password));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgot_password.setText(content);
    }

    @Override
    @OnClick(R2.id.forgot_password)
    public void changePassword() {
        if (CommonUtilities.isNetworkAvailable(this)){
            EnterMobileNumberFragment mForgotPasswordFragment = new EnterMobileNumberFragment();
            addFragment(R.id.login_fragment_container,getSupportFragmentManager(),mForgotPasswordFragment,"EnterMobileNumberFragment");
        }else{
            SnackbarUtils.showLongSnackbar(loginParentLayout,  "It seems you are not connected to the internet. Please switch of on your mobile data to login.");
        }
    }

    private void addFragment(int containerViewId, FragmentManager manager, Fragment fragment, String fragmentTag) {
        try {
            final String fragmentName = fragment.getClass().getName();
            Grove.d("addFragment() :: Adding new fragment %s", fragmentName);
            // Create new fragment and transaction
            final FragmentTransaction transaction = manager.beginTransaction();

            transaction.add(containerViewId, fragment, fragmentTag);
            transaction.addToBackStack(fragmentTag);
            new Handler().post(() -> {
                try {
                    transaction.commit();
                } catch (IllegalStateException ex) {
                    Grove.e("Failed to commit Fragment Transaction with exception %s", ex.getMessage());
                }
            });
        } catch (IllegalStateException ex) {
            Grove.e("Failed to add Fragment with exception %s", ex.getMessage());

        }

    }

    /**
     * This function should be called to inform the UI that the Login Task has been completed <b>successfully</b>.
     * The UI update to reflect successful login should be done here.
     *
     * @param loginResponse - The response in the form of {@link LoginResponse} sent by the API.
     */
    @Override
    public void onLoginSuccess(LoginResponse loginResponse) {
        loginPresenter.getMvpInteractor().persistUserData(loginResponse);
        loginPresenter.finishAndMoveToHomeScreen();
    }

    /**
     * This function should be called to inform the UI that the Login Task has been completed <b>unsuccessfully</b>
     * The UI update to reflect unsuccessful login should be done here.
     */
    @Override
    public void onLoginFailed() {
        progressBar.setVisibility(View.GONE);
        SnackbarUtils.showLongSnackbar(content, getString(R.string.login_failed_error));
    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void finishActivity() {
        finish();
    }

    @OnClick(R2.id.login_submit)
    @Override
    public void performLogin() {
        if (CommonUtilities.isNetworkAvailable(this)) {
            if (validateInputs(editTextUsername, editTextPassword)) {
                String username = Objects.requireNonNull(editTextUsername.getText()).toString().trim();
                String password = Objects.requireNonNull(editTextPassword.getText()).toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                loginPresenter.startAuthenticationTask(new LoginRequest(username, password));
            }else{
                styleDisabledButton();
            }
        } else {
            SnackbarUtils.showLongSnackbar(content, getActivityContext().getResources().getString(R.string.internet_not_connected_error_login));
        }
    }

    /**
     * Validates the login credentials inputted by the user and returns appropriate messages in case of failed login attempt.
     *
     * @param editTextUsername - The {@link EditText} in which user is supposed to type in the username.
     * @param editTextPassword - The {@link EditText} in which user is supposed to type in the password.
     * @return a boolean indicating the result of validation.
     */
    @Override
    public boolean validateInputs(EditText editTextUsername, EditText editTextPassword) {
        if (TextUtils.isEmpty(editTextUsername.getText())) {
            editTextUsername.setError(getString(R.string.user_name_empty_error));
            return false;
        }
        if (TextUtils.getTrimmedLength(editTextUsername.getText()) < 3) {
            editTextUsername.setError(getString(R.string.user_name_not_apt_length_error));
            return false;
        }
        if (TextUtils.isEmpty(editTextPassword.getText())) {
            editTextPassword.setError(getString(R.string.empty_password_error));
            return false;
        }
        if (TextUtils.getTrimmedLength(editTextPassword.getText()) < 8) {
            editTextPassword.setError(getString(R.string.short_password_error));
            return false;
        }
        return true;
    }


    @OnTextChanged(R2.id.login_username)
    public void onUsernameChanged(CharSequence text){
        editTextUsername.setError(null);
        styleLiveButton();
    }

    @OnTextChanged(R2.id.login_password)
    public void onPasswordChanged(CharSequence text){
        editTextPassword.setError(null);
        styleLiveButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        loginPresenter.onDetach();
    }

    public void styleLiveButton(){
        submitButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        submitButton.setTextColor(getResources().getColor(R.color.white));
    }

    public void styleDisabledButton(){
        submitButton.setBackgroundColor(getResources().getColor(R.color.grey));
        submitButton.setTextColor(getResources().getColor(R.color.white));
    }
}
