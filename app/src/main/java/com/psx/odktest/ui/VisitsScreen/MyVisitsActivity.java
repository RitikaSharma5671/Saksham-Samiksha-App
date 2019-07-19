package com.psx.odktest.ui.VisitsScreen;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.psx.odktest.R;
import com.psx.odktest.base.BaseActivity;

import org.odk.collect.android.ODKDriver;

import javax.inject.Inject;

public class MyVisitsActivity extends BaseActivity implements MyVisitsMvpView {

    private LinearLayout viewSubmittedFormsLinearLayout;
    private LinearLayout viewVisitStatusLinearLayout;

    @Inject
    MyVisitsPresenter<MyVisitsMvpView, MyVisitMvpInteractor> visitsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_visits);
        getActivityComponent().inject(this);
        visitsPresenter.onAttach(this);
        setupToolbar();
        findViews();
        setupListeners();
    }

    private void findViews() {
        viewSubmittedFormsLinearLayout = findViewById(R.id.view_submitted_forms);
        viewVisitStatusLinearLayout = findViewById(R.id.view_visit_status);
    }

    private void setupListeners() {
        viewVisitStatusLinearLayout.setOnClickListener(v -> visitsPresenter.onViewVisitStatusClicked(v));
        viewSubmittedFormsLinearLayout.setOnClickListener(v -> visitsPresenter.onViewSubmittedFormsClicked(v));
    }

    @Override
    protected void onResume() {
        super.onResume();
        customizeToolbar();
    }

    private void customizeToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        visitsPresenter.onDetach();
    }

    /**
     * Only set the title and action bar here; do not make modifications.
     * Any further modifications done to the toolbar here will be overwritten if you
     * use {@link ODKDriver}. If you wish to prevent modifications
     * from being overwritten, do them after onCreate is complete.
     */
    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(org.odk.collect.android.R.id.toolbar);
        toolbar.setTitle("My Visits");
        setSupportActionBar(toolbar);
    }
}
