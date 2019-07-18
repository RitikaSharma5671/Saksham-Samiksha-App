package com.psx.odktest.ui.VisitsScreen;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.psx.odktest.R;
import com.psx.odktest.base.BaseActivity;

public class MyVisitsActivity extends BaseActivity implements MyVisitsMvpView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_visits);
        setupToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        customizeToolbar();
    }

    /**
     * Only set the title and action bar here; do not make modifications.
     * Any further modifications done to the toolbar here will be overwritten if you use ODKDriver.
     * If you wish to prevent modifications from being overwritten, do them after onCreate is complete.
     */
    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(org.odk.collect.android.R.id.toolbar);
        toolbar.setTitle("My Visits");
        setSupportActionBar(toolbar);
    }

    private void customizeToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
