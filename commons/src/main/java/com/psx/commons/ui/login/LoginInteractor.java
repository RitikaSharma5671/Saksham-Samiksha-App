package com.psx.commons.ui.login;

import com.psx.commons.base.BaseInteractor;
import com.psx.commons.data.prefs.CommonsPreferenceHelper;

import javax.inject.Inject;

public class LoginInteractor extends BaseInteractor implements LoginContract.Interactor {

    @Inject
    public LoginInteractor(CommonsPreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }
}
