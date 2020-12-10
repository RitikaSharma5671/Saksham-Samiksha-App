package com.samagra.ancillaryscreens.screens.login;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.base.BaseActivity;
import com.samagra.ancillaryscreens.data.network.model.LoginRequest;
import com.samagra.ancillaryscreens.data.network.model.LoginResponse;
import com.example.assets.uielements.MultiTextWatcher;
import com.samagra.ancillaryscreens.screens.passReset.EnterMobileNumberFragment;
import com.samagra.ancillaryscreens.utils.SnackbarUtils;
import com.samagra.commons.CommonUtilities;
import com.samagra.grove.logging.Grove;

import org.odk.collect.android.activities.WebViewActivity;
import org.odk.collect.android.utilities.CustomTabHelper;

import java.util.Objects;

import javax.inject.Inject;

import static com.samagra.commons.CustomTabHelper.OPEN_URL;

/**
 * The View Part for the Login Screen, must implement {@link LoginContract.View}
 *
 * @author Pranav Sharma
 */
@SuppressWarnings("ConstantConditions")
public class LoginActivity extends BaseActivity implements LoginContract.View,MultiTextWatcher.TextWatcherWithInstance {

    public RelativeLayout parentLoginLayout;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextInputEditText userNameEditText;
    private TextInputEditText passwordEditText;
    private Button button;
    private ProgressDialog mProgress;
    private Button helpBtton;
    private TextView mentorFAQ;
    private TextView teacherFAQ;


    @Inject
    LoginPresenter<LoginContract.View, LoginContract.Interactor> loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        getActivityComponent().inject(this);
        loginPresenter.onAttach(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        userNameEditText = findViewById(R.id.edit_user_email);
        helpBtton = findViewById(R.id.help_button);
        teacherFAQ = findViewById(R.id.teacher_faq);
        mentorFAQ = findViewById(R.id.mentor_faq);
        TextView forgotPasswordCTA = findViewById(R.id.forgot_password_cta);
        parentLoginLayout = findViewById(R.id.login_parent);
        SpannableString content = new SpannableString(getText(R.string.reset_here));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotPasswordCTA.setText(content);


        SpannableString contentTeacher = new SpannableString(getText(R.string.faqs_for_teachers_school_heads));
        contentTeacher.setSpan(new UnderlineSpan(), 0, contentTeacher.length(), 0);
        teacherFAQ.setText(contentTeacher);

        SpannableString contentMentor = new SpannableString(getText(R.string.faqs_for_mentors));
        contentMentor.setSpan(new UnderlineSpan(), 0, contentMentor.length(), 0);
        mentorFAQ.setText(contentMentor);

        SpannableString content1 = new SpannableString("NEED HELP?");
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        helpBtton.setText(content1);
        passwordEditText = findViewById(R.id.pwd_field);
        button = findViewById(R.id.login_button);
        new MultiTextWatcher()
                .registerEditText(userNameEditText)
                .registerEditText(passwordEditText)
                .setCallback(this);
        button.setEnabled(false);
        button.setClickable(false);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle(getString(R.string.logging_in));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        String urlFromConfig_MentorDoc = "http://bit.ly/Guidelines_document";
        if(AncillaryScreensDriver.mainApplication.getConfig().getString("faq_mentor_url") != null && !AncillaryScreensDriver.mainApplication.getConfig().getString("faq_mentor_url").isEmpty())
            urlFromConfig_MentorDoc = AncillaryScreensDriver.mainApplication.getConfig().getString("faq_mentor_url");

        String urlFromConfig_TeacherDoc = "http://bit.ly/samiksha-FAQ";
        if(AncillaryScreensDriver.mainApplication.getConfig().getString("faq_teacher_url") != null && !AncillaryScreensDriver.mainApplication.getConfig().getString("faq_teacher_url").isEmpty())
            urlFromConfig_TeacherDoc = AncillaryScreensDriver.mainApplication.getConfig().getString("faq_teacher_url");

        String finalUrlFromConfig_TeacherDoc = urlFromConfig_TeacherDoc;
        teacherFAQ.setOnClickListener(v -> {
            Intent intent = new Intent(getActivityContext(), WebViewActivity.class);
            intent.putExtra(CustomTabHelper.OPEN_URL, finalUrlFromConfig_TeacherDoc);
            getActivityContext().startActivity(intent);
        });
        String finalUrlFromConfig_MentorDoc = urlFromConfig_MentorDoc;
        mentorFAQ.setOnClickListener(v -> {
            Intent intent = new Intent(getActivityContext(), WebViewActivity.class);
            intent.putExtra(CustomTabHelper.OPEN_URL, finalUrlFromConfig_MentorDoc);
            getActivityContext().startActivity(intent);
        });
    }

    @Override
    public void onForgotPasswordClicked(android.view.View v) {
        if (CommonUtilities.isNetworkAvailable(this)){
            EnterMobileNumberFragment mForgotPasswordFragment = new EnterMobileNumberFragment();
            addFragment(R.id.login_fragment_container,getSupportFragmentManager(),
                    mForgotPasswordFragment,"EnterMobileNumberFragment");
        }else{
            SnackbarUtils.showLongSnackbar(parentLoginLayout,  "It seems you are not connected to the internet. Please switch of on your mobile data to login.");
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
        mProgress.dismiss();
        loginPresenter.getIFormManagementContract().resetPreviousODKForms(failedResetActions -> {
            Grove.d("Failure to reset actions at Login screen " + failedResetActions);
            Grove.d("Moving to Home Screen");
            loginPresenter.getIFormManagementContract().createODKDirectories();
            loginPresenter.finishAndMoveToHomeScreen();
        });
    }

    /**
     * This function should be called to inform the UI that the Login Task has been completed <b>unsuccessfully</b>
     * The UI update to reflect unsuccessful login should be done here.
     */
    @Override
    public void onLoginFailed(String loginFailureError) {
        mProgress.dismiss();
        button.setClickable(true);
        button.setEnabled(true);
        SnackbarUtils.showLongSnackbar(parentLoginLayout, loginFailureError);
    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {
    }

    private boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            textInputEmail.setError(getString(R.string.user_name_empty_error));
            return false;
        }else if(emailInput.length() > 0 && emailInput.length() <=3){
            textInputEmail.setError(getText(R.string.user_name_not_apt_length_error));
            return false;
        } else{
            textInputEmail.setError(null);
            textInputEmail.setHelperText(getText(R.string.enter_your_username));
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            textInputPassword.setError(getText(R.string.pass_cannot_be_empty));
            return false;
        } else  if(passwordInput.length() <8) {
            textInputPassword.setError(getText(R.string.pass_less_than_8_error));
            return false;
        }else{
            textInputPassword.setError(null);
            textInputPassword.setHelperText(getText(R.string.enter_your_password));
            return true;
        }
    }

    @Override
    public void onLoginButtonClicked(View v) {
        if (!validateEmail()  | !validatePassword()) {
            return;
        }
        if (CommonUtilities.isNetworkAvailable(this)) {
            String username = Objects.requireNonNull(userNameEditText.getText()).toString().trim();
            String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
            mProgress.show();
            button.setEnabled(false);
            button.setClickable(false);
            loginPresenter.startAuthenticationTask(new LoginRequest(username, password));
        } else {
            SnackbarUtils.showLongSnackbar(parentLoginLayout, LoginActivity.this.getResources().getString(R.string.not_connected_to_internet));
        }
    }

    @Override
    public void onHelpButtonClicked(View v) {
        String urlFromConfig = AncillaryScreensDriver.mainApplication.getConfig().getString("help_url");
        if(urlFromConfig.isEmpty())
            urlFromConfig = "https://forms.gle/ReS5tMBVwpmCMhEe7";
        Uri websiteUri = Uri.parse(urlFromConfig);
            try {
                //open in external browser
                getActivityContext().startActivity(new Intent(Intent.ACTION_VIEW, websiteUri));
            } catch (ActivityNotFoundException | SecurityException e) {
                //open in webview
                Intent intent = new Intent(getActivityContext(), WebViewActivity.class);
                intent.putExtra(OPEN_URL, websiteUri.toString());
                getActivityContext().startActivity(intent);
            }
    }

    @Override
    public void finishActivity() {
        finish();
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

    @Override
    public void beforeTextChanged(EditText editText, CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(EditText editText, CharSequence s, int start, int before, int count) {
        validateLoginButton();
    }

    @Override
    public void afterTextChanged(EditText editText, Editable editable) {
        validateLoginButton();
    }

    @Override
    public void validateLoginButton() {
        if(passwordEditText.getText() != null && passwordEditText.getText().toString().length() > 0 &&
                userNameEditText.getText() != null && userNameEditText.getText().toString().length() > 0){
            button.setEnabled(true);
            button.setClickable(true);
            button.setTextColor(ContextCompat.getColor(this, R.color.white));
        }else{
            button.setEnabled(false);
            button.setClickable(false);
            button.setTextColor(ContextCompat.getColor(this, R.color.color1));
            textInputPassword.setError(null);
            textInputPassword.setHelperText(getText(R.string.enter_your_password));
            textInputEmail.setError(null);
            textInputEmail.setHelperText(getText(R.string.enter_your_username));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.onDetach();
    }
}
