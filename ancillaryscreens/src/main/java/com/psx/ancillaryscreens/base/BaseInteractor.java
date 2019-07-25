package com.psx.ancillaryscreens.base;

import com.psx.ancillaryscreens.data.prefs.CommonsPreferenceHelper;

import javax.inject.Inject;

public class BaseInteractor implements MvpInteractor {
    private final CommonsPreferenceHelper preferenceHelper;

    @Inject
    public BaseInteractor(CommonsPreferenceHelper preferenceHelper) {
        this.preferenceHelper = preferenceHelper;
    }

    @Override
    public CommonsPreferenceHelper getPreferenceHelper() {
        return preferenceHelper;
    }
}
