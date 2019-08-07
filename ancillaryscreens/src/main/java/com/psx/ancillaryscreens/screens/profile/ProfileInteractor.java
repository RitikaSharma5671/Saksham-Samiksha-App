package com.psx.ancillaryscreens.screens.profile;

import com.psx.ancillaryscreens.base.BaseInteractor;
import com.psx.ancillaryscreens.data.prefs.CommonsPreferenceHelper;

import javax.inject.Inject;

public class ProfileInteractor extends BaseInteractor implements ProfileContract.Interactor {

    @Inject
    public ProfileInteractor(CommonsPreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }
}
