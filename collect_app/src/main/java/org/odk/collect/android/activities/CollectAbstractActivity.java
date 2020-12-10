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
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import org.odk.collect.android.ODKDriver;
import org.odk.collect.android.R;
import org.odk.collect.android.utilities.LocaleHelper;
import org.odk.collect.android.utilities.ThemeUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.samagra.commons.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static org.odk.collect.android.utilities.PermissionUtils.areStoragePermissionsGranted;
import static org.odk.collect.android.utilities.PermissionUtils.finishAllActivities;

public abstract class CollectAbstractActivity extends AppCompatActivity {

    private boolean isInstanceStateSaved;
    protected ThemeUtils themeUtils;

    private void modifyToolbarUsingModificationMap(HashMap<String, Object> toolbarModificationObject) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (toolbarModificationObject.get(Constants.CUSTOM_TOOLBAR_TITLE) != null) {
                actionBar.setTitle((String) toolbarModificationObject.get(Constants.CUSTOM_TOOLBAR_TITLE));
            }
            if (!(boolean) toolbarModificationObject.get(Constants.CUSTOM_TOOLBAR_SHOW_NAVICON)) {
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
            } else {
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_cross_white));
                if ((boolean) toolbarModificationObject.get(Constants.CUSTOM_TOOLBAR_BACK_NAVICON_CLICK)) {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text_color));
                    if (toolbar != null) {
                        toolbar.setNavigationOnClickListener(v -> finish());
                    }
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private void modifyToolbarWithGlobalDefualts() {
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
    @Override
    @SuppressWarnings("unchecked")
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra(Constants.KEY_CUSTOMIZE_TOOLBAR)) {
            modifyToolbarUsingModificationMap((HashMap<String, Object>) getIntent().getSerializableExtra(Constants.KEY_CUSTOMIZE_TOOLBAR));
        } else {
            modifyToolbarWithGlobalDefualts();
        }
    }
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
    protected void onSaveInstanceState(Bundle outState) {
        isInstanceStateSaved = true;
        super.onSaveInstanceState(outState);
    }

    public boolean isInstanceStateSaved() {
        return isInstanceStateSaved;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        applyOverrideConfiguration(new Configuration());
    }

    @Override
    public void applyOverrideConfiguration(Configuration newConfig) {
        super.applyOverrideConfiguration(updateConfigurationIfSupported(newConfig));
    }

    private Configuration updateConfigurationIfSupported(Configuration config) {
        if (Build.VERSION.SDK_INT >= 24) {
            if (!config.getLocales().isEmpty()) {
                return config;
            }
        } else {
            if (config.locale != null) {
                return config;
            }
        }

        Locale locale = new LocaleHelper().getLocale(this);
        if (locale != null) {
            config.setLocale(locale);
            config.setLayoutDirection(locale);
        }
        return config;
    }

    public void initToolbar(CharSequence title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }
    }

    /**
     * Checks to see if an activity is one of the entry points to the app i.e
     * an activity that has a view action that can launch the app.
     *
     * @param activity that has permission requesting code.
     * @return true if the activity is an entry point to the app.
     */
    public static boolean isEntryPointActivity(CollectAbstractActivity activity) {

        List<Class<?>> activities = new ArrayList<>();
        activities.add(FormEntryActivity.class);
        activities.add(InstanceChooserList.class);
        activities.add(FillBlankFormActivity.class);
        activities.add(InstanceUploaderListActivity.class);
        activities.add(SplashScreenActivity.class);
        activities.add(FormDownloadListActivity.class);
        activities.add(InstanceUploaderActivity.class);

        for (Class<?> act : activities) {
            if (activity.getClass().equals(act)) {
                return true;
            }
        }

        return false;
    }
}
