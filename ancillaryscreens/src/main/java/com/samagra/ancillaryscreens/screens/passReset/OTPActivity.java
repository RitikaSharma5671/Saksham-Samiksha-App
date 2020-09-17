package com.samagra.ancillaryscreens.screens.passReset;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.base.BaseActivity;

public class OTPActivity extends BaseActivity implements OTPCallBackListener {

    public OTPCallBackListener otpCallBackListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        // Open fragment to get phone number and send OTP
        // => New fragment to enter OTP and new password.
        // => Snackbar to get the results and redirect to login page.

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            if(getIntent().getStringExtra("last").equals("profile")) {
                OTPFragment firstFragment = new OTPFragment();
                firstFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, firstFragment).commit();
            }else{
                EnterMobileNumberFragment_NewUser firstFragment = new EnterMobileNumberFragment_NewUser();
                firstFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, firstFragment).commit();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.getBackStackEntryAt(0);
            if (fm.getBackStackEntryAt(0).getName() != null && (fm.getBackStackEntryAt(0).getName().equals("UpdateAppLanguageFragment")
                    ||  fm.getBackStackEntryAt(0).getName().equals("OTPViewFragment") ||
                    fm.getBackStackEntryAt(0).getName().equals("EnterMobileNumberFragment_NewUser"))) {
                finish();
            } else {
                super.onBackPressed();
            }

        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void Update() {
        this.finish();
    }
}
