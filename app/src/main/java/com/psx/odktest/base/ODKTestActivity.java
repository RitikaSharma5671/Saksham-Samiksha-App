package com.psx.odktest.base;

public interface ODKTestActivity {

    /**
     * Only set the title and action bar here; do not make modifications.
     * Any further modifications done to the toolbar here will be overwritten if you
     * use {@link org.odk.collect.android.ODKDriver}. If you wish to prevent modifications
     * from being overwritten, do them after onCreate is complete.
     */
    void setupToolbar();
}
