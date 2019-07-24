package com.psx.odktest.ui.ComingSoon;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.psx.odktest.R;
import com.psx.odktest.base.NonMvpBaseActivity;
import com.psx.odktest.base.ODKTestActivity;

import org.odk.collect.android.ODKDriver;
import org.odk.collect.android.preferences.GeneralKeys;

public class ComingSoon extends NonMvpBaseActivity implements ODKTestActivity {

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);
        if (getIntent() != null && getIntent().hasExtra(GeneralKeys.TITLE))
            title = getIntent().getStringExtra(GeneralKeys.TITLE);
        setupToolbar();
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
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }
}
