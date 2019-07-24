package com.psx.commons.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.psx.commons.R;
import com.psx.commons.R2;
import com.psx.commons.base.BaseActivity;
import com.psx.commons.base.MvpView;
import com.psx.commons.utils.CommonUtilities;
import com.psx.commons.utils.SnackbarUtils;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    @BindView(R2.id.login_username)
    public EditText editTextUsername;
    @BindView(R2.id.login_password)
    public EditText editTextPassword;
    @BindView(R2.id.circularProgressBar)
    public ProgressBar progressBar;
    @BindView(android.R.id.content)
    public FrameLayout content;

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
    }


    @OnClick(R2.id.forgot_password)
    void changePassword() {
        Intent changePassIntent = new Intent(LoginActivity.this, LoginActivity.class /*TODO: Change to ChangePasswordActivity.claass*/);
        startActivity(changePassIntent);
    }

    @OnClick(R2.id.login_submit)
    void performLogin() {
        if (CommonUtilities.isNetworkAvailable(this)) {

            if (validateInputs(editTextUsername, editTextPassword)) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                //new AuthenticationTask(LoginActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Credentials(username, password));
            } else {
                // Show snackbar that the inputs cannot be empty
                SnackbarUtils.showLongSnackbar(content, "Username or Password cannot be blank.");
            }
        } else
            SnackbarUtils.showLongSnackbar(content, "It seems you are not connected to the Internet. Please switch on your Mobile Data to login.");
    }

    /**
     * Validates the login credentials inputted by the user and returns appropriate messages in case of failed login attempt.
     *
     * @param editTextUsername - The {@link EditText} in which user is supposed to type in the username.
     * @param editTextPassword - The {@link EditText} in which user is supposed to type in the password.
     * @return a boolean indicating the result of validation.
     */
    private boolean validateInputs(EditText editTextUsername, EditText editTextPassword) {
        if (TextUtils.isEmpty(editTextUsername.getText())) {
            editTextUsername.setError("Username cannot be empty");
            return false;
        }
        if (TextUtils.getTrimmedLength(editTextUsername.getText()) < 3) {
            editTextUsername.setError("Username has to be 3 or more characters");
            return false;
        }
        if (TextUtils.isEmpty(editTextPassword.getText())) {
            editTextPassword.setError("Password cannot be empty");
            return false;
        }
        if (TextUtils.getTrimmedLength(editTextPassword.getText()) < 8) {
            editTextPassword.setError("Password has to be at least 8 characters");
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        loginPresenter.onDetach();
    }
}
