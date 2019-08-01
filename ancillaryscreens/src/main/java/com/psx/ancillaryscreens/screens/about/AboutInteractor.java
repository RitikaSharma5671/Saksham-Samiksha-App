package com.psx.ancillaryscreens.screens.about;

import com.psx.ancillaryscreens.base.BaseInteractor;
import com.psx.ancillaryscreens.data.prefs.CommonsPreferenceHelper;

import javax.inject.Inject;

public class AboutInteractor extends BaseInteractor implements AboutContract.Interactor {

    @Inject
    public AboutInteractor(CommonsPreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }
}
