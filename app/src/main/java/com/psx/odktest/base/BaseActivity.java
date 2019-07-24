package com.psx.odktest.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.psx.odktest.MyApplication;
import com.psx.odktest.di.component.ActivityComponent;
import com.psx.odktest.di.component.DaggerActivityComponent;
import com.psx.odktest.di.modules.ActivityModule;

import org.odk.collect.android.activities.CollectAbstractActivity;

public abstract class BaseActivity extends CollectAbstractActivity implements MvpView {

    private ActivityComponent activityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(MyApplication.get(this).getApplicationComponent())
                    .build();
        }
        return activityComponent;
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void showSnackbar(String message, int duration) {
        Snackbar.make(findViewById(android.R.id.content), message, duration).show();
    }
}
