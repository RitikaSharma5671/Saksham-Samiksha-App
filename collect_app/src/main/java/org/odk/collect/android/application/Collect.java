/*
 * Copyright (C) 2017 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.application;

public class Collect {
//        extends Application implements LocalizedApplication {
//    private static Collect singleton;

//    @Nullable
//    private FormController formController;
//    private ExternalDataManager externalDataManager;
//    private AppDependencyComponent applicationComponent;
//
//    @Inject
//    ApplicationInitializer applicationInitializer;
//
//    @Inject
//    PreferencesProvider preferencesProvider;
//
//    public static Collect getInstance() {
//        return singleton;
//    }



    /*
//        Adds support for multidex support library. For more info check out the link below,
//        https://developer.android.com/studio/build/multidex.html
//    */
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }


//    /**
//     * Enable StrictMode and log violations to the system log.
//     * This catches disk and network access on the main thread, as well as leaked SQLite
//     * cursors and unclosed resources.
//     */
//    private void setupStrictMode() {
//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectAll()
//                    .permitDiskReads()  // shared preferences are being read on main thread
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build());
//        }
//    }
//
//    private void setupDagger() {
//        applicationComponent = DaggerAppDependencyComponent.builder()
//                .application(this)
//                .build();
//
//        applicationComponent.inject(this);
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        //noinspection deprecation
//        defaultSysLanguage = newConfig.locale.getLanguage();
//    }
//
//    public AppDependencyComponent getComponent() {
//        return applicationComponent;
//    }
//
//    public void setComponent(AppDependencyComponent applicationComponent) {
//        this.applicationComponent = applicationComponent;
//        applicationComponent.inject(this);
//    }

    /**
     * Gets a unique, privacy-preserving identifier for the current form.
     *
//     * @return md5 hash of the form title, a space, the form ID
//     */
//    public static String getCurrentFormIdentifierHash() {
//        FormController formController = getInstance().getFormController();
//        if (formController != null) {
//            return formController.getCurrentFormIdentifierHash();
//        }
//
//        return "";
//    }

    /**
//     * Gets a unique, privacy-preserving identifier for a form based on its id and version.
//     * @param formId id of a form
//     * @param formVersion version of a form
//     * @return md5 hash of the form title, a space, the form ID
//     */
//    public static String getFormIdentifierHash(String formId, String formVersion) {
//        String formIdentifier = new FormsDao().getFormTitleForFormIdAndFormVersion(formId, formVersion) + " " + formId;
//        return FileUtils.getMd5Hash(new ByteArrayInputStream(formIdentifier.getBytes()));
//    }

//    // https://issuetracker.google.com/issues/154855417
//    private void fixGoogleBug154855417() {
//        try {
//            SharedPreferences metaSharedPreferences = preferencesProvider.getMetaSharedPreferences();
//
//            boolean hasFixedGoogleBug154855417 = metaSharedPreferences.getBoolean(KEY_GOOGLE_BUG_154855417_FIXED, false);
//
//            if (!hasFixedGoogleBug154855417) {
//                File corruptedZoomTables = new File(getFilesDir(), "ZoomTables.data");
//                corruptedZoomTables.delete();
//
//                metaSharedPreferences
//                        .edit()
//                        .putBoolean(KEY_GOOGLE_BUG_154855417_FIXED, true)
//                        .apply();
//            }
//        } catch (Exception ignored) {
//            // ignored
//        }
//    }
//
//    @NotNull
//    @Override
//    public Locale getLocale() {
//        return new Locale(LocaleHelper.getLocaleCode(this));
//    }
}
