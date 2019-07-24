package com.psx.odktest.base;

import com.psx.odktest.data.prefs.PreferenceHelper;

import javax.inject.Inject;

public class BaseInteractor implements MvpInteractor {
    private final PreferenceHelper preferenceHelper;

    @Inject
    public BaseInteractor(PreferenceHelper preferenceHelper) {
        this.preferenceHelper = preferenceHelper;
    }

    @Override
    public PreferenceHelper getPreferenceHelper() {
        return preferenceHelper;
    }
}
