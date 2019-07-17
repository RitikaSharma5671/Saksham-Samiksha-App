package com.psx.odktest;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.psx.odktest.base.BaseActivity;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Home");
        setSupportActionBar(toolbar);
    }
}
