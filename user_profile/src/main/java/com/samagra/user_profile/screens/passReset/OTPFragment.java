package com.samagra.user_profile.screens.passReset;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.samagra.user_profile.R;
import com.samagra.user_profile.screens.change_password.ActionListener;
import com.samagra.user_profile.screens.change_password.SnackbarUtils;


public class OTPFragment extends Fragment implements View.OnClickListener, ActionListener {

    OTPCallBackListener mCallback;

    private EditText password;
    private EditText confirmPassword;
    private EditText otp;
    private String phoneNumber;
    private View parent;
    private String lastPage = "lastPage";
    String TAG = OTPFragment.class.getName();
    private String updatePasswordURL;
    private String applicationID;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OTPCallBackListener) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otp_view, container, false);
        password = view.findViewById(R.id.new_password);
        confirmPassword = view.findViewById(R.id.confirm_password);
        otp = view.findViewById(R.id.otp);
        parent = view.findViewById(R.id.parent_ll);
        ImageView iv = view.findViewById(R.id.otp_govt_logo);
        iv.setImageResource(R.drawable.govt_logo);

        String title = "Set New Password";
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view1 -> {
            if (lastPage.equals("profile")) {
                mCallback.Update();
            } else {
                getFragmentManager().popBackStackImmediate();
            }
        });

        Bundle arguments = getArguments();

        if (arguments != null) {
            phoneNumber = arguments.getString("phoneNumber");
            //lastPage = arguments.getString("last");
            if (arguments.getString("last") != null) {
                lastPage = arguments.getString("last");
            }
            if(arguments.getString("updatePasswordURL") != null) {
                updatePasswordURL = arguments.getString("updatePasswordURL");
                applicationID = arguments.getString("applicationID");
            }
        }

        Button button = view.findViewById(R.id.password_submit);
        password.addTextChangedListener(getWatcher(otp, password, confirmPassword, button));
        confirmPassword.addTextChangedListener(getWatcher(otp, password, confirmPassword, button));

        button.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.password_submit) {
            String pass = password.getText().toString();
            String confPass = confirmPassword.getText().toString();
            if (pass.equals(confPass)) {
                new UpdatePasswordTask(this, updatePasswordURL, applicationID).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, phoneNumber,
                        otp.getText().toString(),
                        pass);
            } else {
                SnackbarUtils.showLongSnackbar(parent, "Passwords didn't match.");
            }
        }
    }

    @Override
    public void onSuccess() {
        // Return to login screen. Show snackbar that password was changed successfully.
        // Logout if not logged out and ask him to login again.

        //check String LastPage for profile to redirect to Profile.

        if (lastPage.equals("profile")) {
            SnackbarUtils.showLongSnackbar(parent, "The password has been successfully changed. " +
                    "Redirecting you to profile page");
            mCallback.Update();
            return;
        } else {
            SnackbarUtils.showLongSnackbar(parent, "The password has been successfully changed. " +
                    "You need to login again to continue. Redirecting you to login page");

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Logout the user.
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                String token = sharedPreferences.getString("FCM.token", "");
                if (token.length() > 0) {
                    // new PushMessagingService().setContext(getActivity()).removeTokenFromServer(token);
                }
//                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        }, 5000);
    }

    @Override
    public void onFailure(Exception exception) {
        if (parent != null)
            SnackbarUtils.showLongSnackbar(parent, "The server responded with the following issue: " + exception.getMessage());

    }

    private TextWatcher getWatcher(EditText otp, EditText password, EditText confirmPassword, Button login) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String _otp = otp.getText().toString().trim();
                String _password = password.getText().toString().trim();
                String _confirmPassword = confirmPassword.getText().toString().trim();
                if (validateInputs(_otp, _password, _confirmPassword)) {
                    login.setBackgroundColor(getResources().getColor(R.color.button_colors));
                    login.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.button_colors), PorterDuff.Mode.MULTIPLY);
                    login.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                } else {
                    login.setBackgroundColor(getResources().getColor(R.color.lightDividerColor));
                    login.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.lightDividerColor), PorterDuff.Mode.MULTIPLY);
                    login.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private boolean validateInputs(String otp, String password, String confirmPassword) {
        if (otp.length() == 4
                && password.length() >= 8
                && confirmPassword.equals(password)) return true;
        return false;
    }
}

