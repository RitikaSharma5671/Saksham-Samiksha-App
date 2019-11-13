package com.samagra.odktest.ui.VisitsScreen;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.samagra.odktest.R;
import com.samagra.odktest.base.BaseActivity;

import org.odk.collect.android.ODKDriver;

import javax.inject.Inject;

/**
 * View part of the MyVisits Screen. This class only handles the UI operations, all the business
 * logic is simply abstracted from this Activity. It <b>must</b> implement the {@link MyVisitsMvpView}
 * and extend the {@link BaseActivity}.
 *
 * @author Pranav Sharma
 */
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
     * Only set the title and action bar here; do not make further modifications.
     * Any further modifications done to the toolbar here will be overwritten if you
     * use {@link ODKDriver}. If you wish to prevent modifications
     * from being overwritten, do them after onCreate is complete.
     * This method should be called in onCreate of your activity.
     */
    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(org.odk.collect.android.R.id.toolbar);
        toolbar.setTitle("My Visits");
        setSupportActionBar(toolbar);
    }
}
