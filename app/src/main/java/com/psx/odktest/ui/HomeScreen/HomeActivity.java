package com.psx.odktest.ui.HomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.psx.odktest.R;
import com.psx.odktest.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeActivity extends BaseActivity implements HomeMvpView, View.OnClickListener {

    @BindView(R.id.welcome_text)
    public TextView welcomeTextView;
    @BindView(R.id.helpline_button)
    public Button helplineButton;
    @BindView(R.id.submit_form)
    public LinearLayout submitFormLinearLayout;
    @BindView(R.id.inspect_school)
    public LinearLayout inspectSchoolLinearLayout;
    @BindView(R.id.view_filled_forms)
    public LinearLayout viewFilledFormsLinearLayout;
    @BindView(R.id.view_issues)
    public LinearLayout viewIssuesLinearLayout;


    private Unbinder unbinder;

    @Inject
    HomePresenter<HomeMvpView, HomeMvpInteractor> homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getActivityComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        homePresenter.onAttach(this);
        setupToolbar();
        setupListeners();
        checkIntent();
        homePresenter.setWelcomeText();
        homePresenter.applySettings();
    }

    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Home");
        setSupportActionBar(toolbar);
    }

    private void setupListeners() {
        helplineButton.setOnClickListener(this);
        submitFormLinearLayout.setOnClickListener(this);
        inspectSchoolLinearLayout.setOnClickListener(this);
        viewFilledFormsLinearLayout.setOnClickListener(this);
        viewIssuesLinearLayout.setOnClickListener(this);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("ShowSnackbar", false)) {
            if (homePresenter.isNetworkConnected())
                showSnackbar(getString(R.string.on_internet_saving_complete), Snackbar.LENGTH_SHORT);
            else
                showSnackbar(getString(R.string.no_internet_saving_complete), Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.helpline_button:
                homePresenter.onHelplineButtonClicked(v);
                break;
            case R.id.submit_form:
                homePresenter.onSubmitFormClicked(v);
                break;
            case R.id.inspect_school:
                homePresenter.onInspectSchoolClicked(v);
                break;
            case R.id.view_filled_forms:
                homePresenter.onMyVisitClicked(v);
                break;
            case R.id.view_issues:
                homePresenter.onViewIssuesClicked(v);
                break;
        }
    }

    @Override
    public void updateWelcomeText(String text) {
        welcomeTextView.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        homePresenter.onDetach();
        unbinder.unbind();
    }
}
