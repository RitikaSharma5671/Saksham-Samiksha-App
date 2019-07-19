package com.psx.odktest.ui.SearchActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import com.psx.odktest.R;
import com.psx.odktest.base.BaseActivity;

import org.odk.collect.android.ODKDriver;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchActivity extends BaseActivity implements SearchMvpView {

    @BindView(R.id.search_bar)
    public EditText searchBarEditText;
    @BindView(R.id.district_spinner)
    public Spinner districtSpinner;
    @BindView(R.id.block_spinner)
    public Spinner blockSpinner;
    @BindView(R.id.cluster_spinner)
    public Spinner clusterSpinner;
    @BindView(R.id.school_spinner)
    public Spinner schoolSpinner;
    @BindView(R.id.next_button)
    public Button nextButton;

    private String selectedDistrict, selectedBlock, selectedCluster, selectedSchoolName;

    @Inject
    SearchPresenter<SearchMvpView, SearchMvpInteractor> searchPresenter;
    private Unbinder unbinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getActivityComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        searchPresenter.onAttach(this);
        setupToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        customizeToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("School Selection");
        setSupportActionBar(toolbar);
    }

    private void customizeToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        searchPresenter.onDetach();
    }
}
