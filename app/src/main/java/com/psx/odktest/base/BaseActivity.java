package com.psx.odktest.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.odk.collect.android.activities.CollectAbstractActivity;

public abstract class BaseActivity extends CollectAbstractActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
