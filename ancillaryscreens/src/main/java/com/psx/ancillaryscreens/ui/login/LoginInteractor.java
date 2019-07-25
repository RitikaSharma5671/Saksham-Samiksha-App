package com.psx.ancillaryscreens.ui.login;

import com.psx.ancillaryscreens.base.BaseInteractor;
import com.psx.ancillaryscreens.data.prefs.CommonsPreferenceHelper;

import javax.inject.Inject;

public class LoginInteractor extends BaseInteractor implements LoginContract.Interactor {

    @Inject
    public LoginInteractor(CommonsPreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }
}
