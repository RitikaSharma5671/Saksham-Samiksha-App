/*
 * Copyright 2017 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odk.collect.android.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.odk.collect.android.ODKDriver;
import org.odk.collect.android.R;
import org.odk.collect.android.utilities.LocaleHelper;
import org.odk.collect.android.utilities.ThemeUtils;

import timber.log.Timber;

import static org.odk.collect.android.utilities.PermissionUtils.finishAllActivities;
import static org.odk.collect.android.utilities.PermissionUtils.isEntryPointActivity;
import static org.odk.collect.android.utilities.PermissionUtils.areStoragePermissionsGranted;

public abstract class CollectAbstractActivity extends AppCompatActivity {

    private boolean isInstanceStateSaved;
    protected ThemeUtils themeUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        themeUtils = new ThemeUtils(this);
        setTheme(this instanceof FormEntryActivity ? themeUtils.getFormEntryActivityTheme() : themeUtils.getAppTheme());
        super.onCreate(savedInstanceState);

        /**
         * If a user has revoked the storage permission then this check ensures the app doesn't quit unexpectedly and
         * informs the user of the implications of their decision before exiting. The app can't function with these permissions
         * so if a user wishes to grant them they just restart.
         *
         * This code won't run on activities that are entry points to the app because those activities
         * are able to handle permission checks and requests by themselves.
         */
        if (!areStoragePermissionsGranted(this) && !isEntryPointActivity(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);

            builder.setTitle(R.string.storage_runtime_permission_denied_title)
                    .setMessage(R.string.storage_runtime_permission_denied_desc)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        finishAllActivities(this);
                    })
                    .setIcon(R.drawable.sd)
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        isInstanceStateSaved = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        modifyToolbar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        isInstanceStateSaved = true;
        super.onSaveInstanceState(outState);
    }

    public boolean isInstanceStateSaved() {
        return isInstanceStateSaved;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(new LocaleHelper().updateLocale(base));
    }

    @SuppressLint("ResourceType")
    private void modifyToolbar() {
        if (getSupportActionBar() != null && ODKDriver.isModifyToolbarIcon()) {
            if (ODKDriver.getToolbarIconResId() == Long.MAX_VALUE) {
                // Hide the Toolbar icon
                Timber.i("Changing toolbar Icon to null");
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                Timber.i("Changing toolbar icon to set Icon");
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator((int) ODKDriver.getToolbarIconResId());
            }
        } else {
            Timber.w("No toolbar found, cannot modify");
        }
    }
}
