package com.psx.odktest.ui.HomeScreen;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.psx.odktest.R;
import com.psx.odktest.base.BaseActivity;

public class HomeActivity extends BaseActivity implements HomeMvpView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupToolbar();
    }

    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Home");
        setSupportActionBar(toolbar);
    }
}
