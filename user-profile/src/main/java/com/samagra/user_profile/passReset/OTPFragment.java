package com.samagra.user_profile.passReset;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;
import com.samagra.commons.SamagraAlertDialog;
import com.samagra.grove.logging.Grove;
import com.samagra.user_profile.R;

import org.odk.collect.android.listeners.ActionListener;

public class OTPFragment extends Fragment implements View.OnClickListener, ActionListener, MultiTextWatcher.TextWatcherWithInstance,ChangePasswordActionListener {

    private TextInputLayout passwordLayout;
    private TextInputLayout otpLayou;
    private TextInputLayout confirmPasswordLayout;
    private EditText password;
    private EditText confirmPassword;
    private EditText otp;
    private String phoneNumber;
    private TextView timer;
    private View parent;
    private String lastPage = "lastPage";
    Button submitButton;
    private View.OnClickListener resendListener;
    String TAG = OTPFragment.class.getName();
    private CountDownTimer countDownTimer;
    private ProgressDialog mProgress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otp_view, container, false);
        Bundle arguments = getArguments();

        if (arguments != null) {
            phoneNumber = arguments.getString("phoneNumber");
            //lastPage = arguments.getString("last");
            if (arguments.getString("lastPage") != null) {
                lastPage = arguments.getString("lastPage");
            }
        }
        password = view.findViewById(R.id.new_password);
        confirmPassword = view.findViewById(R.id.confirm_password);
        passwordLayout = view.findViewById(R.id.password_text);
        otpLayou = view.findViewById(R.id.otp_text);
        confirmPasswordLayout = view.findViewById(R.id.confirm_password_text);
        otp = view.findViewById(R.id.otp);
        parent = view.findViewById(R.id.parent);
        timer = view.findViewById(R.id.countdown_timer);
        ImageView iv = view.findViewById(R.id.otp_govt_logo);
        iv.setImageResource(R.drawable.ic_change_password);
        startTimer();
        String title = this.getResources().getString(R.string.set_new_password);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.white));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view1 -> {
            if (lastPage.equals("profile")) {
                getActivity().finish();
            } else {
                getFragmentManager().popBackStack();
            }
        });

        submitButton = view.findViewById(R.id.password_submit);
        new MultiTextWatcher()
                .registerEditText(otp)
                .registerEditText(password)
                .registerEditText(confirmPassword)
                .setCallback(this);

        resendListener = v -> {
            if (!isNetworkConnected()) {
                if (parent != null)
                    SnackbarUtils.showShortSnackbar(parent, OTPFragment.this.getActivity().getResources().getString(R.string.internet_not_connected));
            } else {
                showProgressBar(getString(R.string.sending_otp));
                new SendOTPTask(new ChangePasswordActionListener() {
                    @Override
                    public void onSuccess() {
                        hideProgressBar();
                        startTimer();
                        if (view != null) {
                            parent = view.findViewById(R.id.parent);
                            if (parent != null) {
                                SnackbarUtils.showLongSnackbar(parent, getString(R.string.otp_sent_change_pwd));
                            }
                        }
                        submitButton.setText(getActivity().getResources().getString(R.string.submit));
                        submitButton.setOnClickListener(OTPFragment.this::onClick);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        hideProgressBar();
                        if (parent != null) {
                            SnackbarUtils.showLongSnackbar(parent, OTPFragment.this.getActivity().getResources().getString(R.string.error_sending_otp));
                        }
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phoneNumber);
            }
        };

        submitButton.setOnClickListener(this);
        mProgress = new ProgressDialog(requireContext());
        mProgress.setTitle(getString(R.string.password_changing));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (!isNetworkConnected()) {
            if (parent != null) {
                SnackbarUtils.showLongSnackbar(parent, this.getResources().getString(R.string.internet_not_connected));
            }
        } else {
            if (v.getId() == R.id.password_submit) {
                String pass = password.getText().toString();
                String confPass = confirmPassword.getText().toString();
                String otpText = otp.getText().toString();
                if (!validateInputs(otpText, pass, confPass)) {
                    return;
                }
                showProgressBar(getString(R.string.password_changing));
                new UpdatePasswordTask(this).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, phoneNumber,
                        otp.getText().toString(),
                        pass);
            }
        }
    }

    @Override
    public void onSuccess() {
        Grove.e(TAG, "Successfully changed password.");
        hideProgressBar();

        // Return to login screen. Show snack-bar that password was changed successfully.
        // Logout if not logged out and ask him to login again.

        //check String LastPage for profile to redirect to Profile.
        if (lastPage.equals("profile")) {
            new SamagraAlertDialog.Builder(requireContext()).setTitle(getText(R.string.password_changed)).
                    setMessage(getText(R.string.pass_changed_redirecting)).
                    setAction2(getText(R.string.ok), (actionIndex, alertDialog) -> {
                        removeFragment(this, getFragmentManager());
                        getActivity().finish();
                        alertDialog.dismiss();
                    }).show();
        } else {
            new SamagraAlertDialog.Builder(requireContext()).setTitle(getText(R.string.password_changed)).
                    setMessage(getText(R.string.pass_change_successful)).
                    setAction2(getText(R.string.ok), (actionIndex, alertDialog) -> {
                        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                        editor1.putBoolean("isLoggedIn", false);
                        editor1.apply();
                        removeFragment(this, getFragmentManager());
//                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        alertDialog.dismiss();
                    }).show();
        }
    }

    private void removeFragment(Fragment fragment, FragmentManager manager) {
        if (fragment == null || manager == null) return;
        try {
            String fragmentName = fragment.getClass().getName();
            Grove.d("removeFragment() :: Removing current fragment %s", fragmentName);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(fragment);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        transaction.commit();
                        manager.popBackStack();
                    } catch (IllegalStateException ex) {
                        //  reportException(new IllegalStateException("Non App crash custom Exception in removeFragment in " + fragmentname,ex));
                    }
                }
            });

        } catch (IllegalStateException ex) {
//            reportException(new IllegalStateException("Non App crash custom Exception in removeFragment",ex));
        }
    }

    @Override
    public void onFailure(Exception exception) {
        hideProgressBar();
        if (parent != null) {
            Grove.e("Could not update Password" + exception.getMessage());
            SnackbarUtils.showLongSnackbar(parent, exception.getMessage());
        }
    }

    private boolean validateInputs(String otp, String password, String confirmPassword) {
        boolean isOtpValid = false;
        boolean isPwdValid = false;
        boolean isConfirmPwdValid = false;
        boolean arePasswordsMatching = false;

        if (otp.trim().length() == 4) {
            isOtpValid = true;
        } else {
            if (otp.trim().length() == 0) {
                otpLayou.setError(getText(R.string.otp_length));
            } else {
                otpLayou.setError(getText(R.string.empty_otp));
            }
        }

        if (password.trim().length() >= 8) {
            isPwdValid = true;
        } else {
            if (password.trim().length() == 0) {
                passwordLayout.setError(getString(R.string.pass_cannot_be_empty));
            } else {
                passwordLayout.setError(getString(R.string.less_than_8_pass));
            }
        }

        if (confirmPassword.trim().length() >= 8) {
            isConfirmPwdValid = true;
        } else {
            if (confirmPassword.trim().length() == 0) {
                confirmPasswordLayout.setError(getString(R.string.pass_cannot_be_empty));
            } else {
                confirmPasswordLayout.setError(getString(R.string.less_than_8_pass));
            }
        }
        if (!password.trim().equals(confirmPassword.trim())) {
            if (parent != null) {
                SnackbarUtils.showLongSnackbar(parent, this.getResources().getString(R.string.pass_did_not_match));
            }
        } else {
            arePasswordsMatching = true;
        }

        return isOtpValid && isPwdValid && isConfirmPwdValid && arePasswordsMatching;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setText(getString(R.string.seconds_remaining) + millisUntilFinished / 1000);
            }

            public void onFinish() {
                submitButton.setText("Resend OTP");
                submitButton.setOnClickListener(resendListener);
            }
        };
        countDownTimer.start();
    }

    private void showProgressBar(String text) {
        parent.setClickable(false);
        mProgress.show();
    }

    private void hideProgressBar() {
        parent.setClickable(true);
        mProgress.dismiss();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    @Override
    public void beforeTextChanged(EditText editText, CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(EditText editText, CharSequence s, int start, int before, int count) {
        validateButton();
    }

    @Override
    public void afterTextChanged(EditText editText, Editable editable) {
        validateButton();
    }

    private void validateButton() {
        if (otp.getText() != null && otp.getText().toString().trim().length() == 4
                && password.getText() != null && password.getText().toString().trim().length() >= 8 &&
                confirmPassword.getText() != null && confirmPassword.getText().toString().trim().length() >= 8 &&
                password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())) {
            submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimar1y));
            submitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        } else {
            submitButton.setBackground(ContextCompat.getDrawable(requireContext(),
                    R.drawable.button1_background_disabled));
            submitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            passwordLayout.setError(null);
            otpLayou.setError(null);
            otpLayou.setHelperText(getText(R.string.enter_otp));
            passwordLayout.setHelperText(getText(R.string.enter_new_password));
            confirmPasswordLayout.setError(null);
            confirmPasswordLayout.setHelperText(getText(R.string.confirm_password));
        }
    }

}
