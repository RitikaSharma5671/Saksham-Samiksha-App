package com.samagra.ancillaryscreens.screens.change_password;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.models.OnUserFound;
import com.samagra.ancillaryscreens.screens.passReset.ChangePasswordActionListener;
import com.samagra.ancillaryscreens.screens.passReset.OTPFragment;
import com.samagra.ancillaryscreens.screens.passReset.SendOTPTask;
import com.samagra.ancillaryscreens.utils.SnackbarUtils;
import com.samagra.commons.SamagraAlertDialog;

import org.jetbrains.annotations.NotNull;
import org.odk.collect.android.fragments.dialogs.ProgressDialogFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener, ChangePasswordActionListener, OnUserFound {

    private EditText phoneNumber;
    private View parent;
    private  Button button;
    private ProgressDialog mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.change_password_view, container, false);

        String title = getActivity().getResources().getString(R.string.forgot_pass);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view1 -> {
            getFragmentManager().popBackStack();
            getActivity().finish();
        });
        button = view.findViewById(R.id.phone_submit);
        phoneNumber = view.findViewById(R.id.user_phone);
        parent = view.findViewById(R.id.rootView);
        button.setOnClickListener(this);
        mProgress = new ProgressDialog(requireContext());
        mProgress.setTitle(getString(R.string.sending_the_request));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.phone_submit) {
            if (validate(phoneNumber.getText().toString())) {
                button.setClickable(false);
                button.setEnabled(false);
                mProgress.show();
                checkPhoneValidity(phoneNumber.getText().toString());
//                new SendOTPTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phoneNumber.getText().toString());
            } else {
                SnackbarUtils.showLongSnackbar(parent, getActivity().getResources().getString(R.string.invalid_phone_number));
                phoneNumber.setText("");
            }
        }
    }

    private void checkPhoneValidity(String phoneNumber) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String token = sharedPreferences.getString("token", "");
        new FindUserByPhoneTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                phoneNumber, token);
    }

    private boolean validate(String phoneNumber) {
        Pattern p = Pattern.compile("[6-9][0-9]{9}");
        Matcher m = p.matcher(phoneNumber);
        return (m.find() && m.group().equals(phoneNumber));
    }

    @Override
    public void onSuccess() {
        OTPFragment otpFragment = new OTPFragment();
        Bundle arguments = new Bundle();
        arguments.putString("phoneNumber", phoneNumber.getText().toString());
        otpFragment.setArguments(arguments);
        mProgress.dismiss();
        button.setEnabled(true);
        button.setClickable(true);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, otpFragment, "NewFragmentTag");
        ft.commit();
        ft.addToBackStack(null);
        if (parent != null) {
            SnackbarUtils.showLongSnackbar(parent, getString(R.string.otp_sent_successfully) + "  " + phoneNumber.getText().toString());
        }
    }

    @Override
    public void onFailure(Exception exception) {
        mProgress.dismiss();
        button.setEnabled(true);
        button.setClickable(true);
        new SamagraAlertDialog.Builder(requireContext()).setImageView(ContextCompat.getDrawable(requireContext(),
                R.drawable.ic_warning_error)).setTitle(getText(R.string.unable_to_send_OTP)).
                setMessage(exception.getMessage()).
                setAction2(getString(R.string.ok), new SamagraAlertDialog.CaastleAlertDialogActionListener() {
                    @Override
                    public void onActionButtonClicked(int actionIndex, @NotNull SamagraAlertDialog alertDialog) {
                        alertDialog.dismiss();
                    }
                });    }


    @Override
    public void onSuccessUserFound(String numberOfUsers) {
        if (Integer.parseInt(numberOfUsers) > 0 && phoneNumber.getText().toString() != null) {
            new SendOTPTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phoneNumber.getText().toString());
        } else {
            button.setClickable(true);
            button.setEnabled(true);
            mProgress.dismiss();
            new SamagraAlertDialog.Builder(requireContext()).setImageTitle(ContextCompat.getDrawable(requireContext(), R.drawable.ic_browser_error))
                    .setTitle("No users found for this number").
                    setMessage(getText(R.string.contact_admin_for_this)).
                    setAction2(getText(R.string.ok), (actionIndex, alertDialog) -> alertDialog.dismiss()
                    ).show();
        }
    }

    @Override
    public void onFailureUserFound(Exception e) {button.setClickable(true);
        button.setEnabled(true);
        button.setClickable(true);
        mProgress.dismiss();
        new SamagraAlertDialog.Builder(requireContext()).setImageView(ContextCompat.getDrawable(requireContext(),
                R.drawable.ic_warning_error)).setTitle(getText(R.string.unable_to_send_OTP)).
                setMessage(e.getMessage()).
                setAction2(getString(R.string.ok), new SamagraAlertDialog.CaastleAlertDialogActionListener() {
                    @Override
                    public void onActionButtonClicked(int actionIndex, @NotNull SamagraAlertDialog alertDialog) {
                        alertDialog.dismiss();
                    }
                });
    }
}
