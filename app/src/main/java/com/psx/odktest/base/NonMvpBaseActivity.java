package com.psx.odktest.base;

import android.view.MenuItem;

import org.odk.collect.android.activities.CollectAbstractActivity;

public abstract class NonMvpBaseActivity extends CollectAbstractActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
