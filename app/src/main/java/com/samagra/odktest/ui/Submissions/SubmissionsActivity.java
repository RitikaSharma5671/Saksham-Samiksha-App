package com.samagra.odktest.ui.Submissions;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.samagra.grove.Grove;
import com.samagra.odktest.R;
import com.samagra.odktest.base.BaseActivity;
import com.samagra.odktest.data.models.Submission;

import org.odk.collect.android.ODKDriver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * View part of the MyVisits Screen. This class only handles the UI operations, all the business
 * logic is simply abstracted from this Activity. It <b>must</b> implement the {@link SubmissionsMvpView}
 * and extend the {@link BaseActivity}.
 *
 * @author Chakshu Gautam
 */
public class SubmissionsActivity extends BaseActivity implements SubmissionsMvpView {

    @BindView(R.id.submissions_rv)
    public RecyclerView recyclerView;

    @BindView(R.id.parent_of_submissions)
    public LinearLayout parentLayout;

    @BindView(R.id.progress_bar)
    public ProgressBar progressBar;

    @BindView(R.id.no_internet_iv)
    public ImageView noInternet;

    public SubmissionsAdapter submissionsAdapter;
    public RecyclerView.LayoutManager layoutManager;
    private Unbinder unbinder;

    private ArrayList<Submission> submissions = new ArrayList<Submission>();

    @Inject
    SubmissionsPresenter<SubmissionsMvpView, SubmissionsMvpInteractor> submissionsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submissions);
        getActivityComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        submissionsPresenter.onAttach(this);
        setupToolbar();
        showProgressBar();
        setupRecyclerView();
        submissionsPresenter.getCachedData();
    }

    public View getParentLayout(){
        return parentLayout;
    }

    void setupRecyclerView(){
        submissions = submissionsPresenter.getSubmissionsFromCache();
        submissionsAdapter = new SubmissionsAdapter(submissions, this);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(submissionsAdapter);
    }

    private void refreshData(ArrayList<Submission> newData) {
        // https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data?rq=1
        submissions.clear();
        submissions.addAll(newData);
        submissionsAdapter.notifyDataSetChanged();
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
        submissionsPresenter.onDetach();
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
        toolbar.setTitle("Submissions");
        setSupportActionBar(toolbar);
    }


    @Override
    public void onRefreshButtonPressed() {

    }

    @Override
    public void hideProgressBar() {
        recyclerView.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar() {
        recyclerView.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void animateSmallProgressBar() {

    }

    @Override
    public void render(ArrayList<Submission> submissions) {
        refreshData(submissions);
        hideProgressBar();
        recyclerView.setVisibility(View.VISIBLE);
        Grove.e("Rendered submissions");
    }

    @Override
    public void renderNoData() {
        recyclerView.setVisibility(View.GONE);
        noInternet.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
