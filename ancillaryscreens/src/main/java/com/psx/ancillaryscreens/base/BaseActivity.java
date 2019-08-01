package com.psx.ancillaryscreens.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.psx.ancillaryscreens.di.component.ActivityComponent;
import com.psx.ancillaryscreens.di.component.DaggerActivityComponent;
import com.psx.ancillaryscreens.di.modules.CommonsActivityModule;

public abstract class BaseActivity extends AppCompatActivity implements MvpView {

    private ActivityComponent activityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .commonsActivityModule(new CommonsActivityModule(this))
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
