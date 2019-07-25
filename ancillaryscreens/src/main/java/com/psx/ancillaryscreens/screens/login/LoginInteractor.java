package com.psx.ancillaryscreens.screens.login;

import com.psx.ancillaryscreens.base.BaseInteractor;
import com.psx.ancillaryscreens.data.network.model.LoginResponse;
import com.psx.ancillaryscreens.data.prefs.CommonsPreferenceHelper;

import javax.inject.Inject;

import timber.log.Timber;

public class LoginInteractor extends BaseInteractor implements LoginContract.Interactor {

    @Inject
    public LoginInteractor(CommonsPreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }

    @Override
    public void persistUserData(LoginResponse result) {
        try {
            getPreferenceHelper().setCurrentUserLoginFlags();
            getPreferenceHelper().setCurrentUserDetailsFromLogin(result);
            getPreferenceHelper().setCurrentUserAdditionalDetailsFromLogin(result);
        } catch (Exception e) {
            Timber.e("Exception in persisting user data in shared prefs %s", e.getMessage());
            Timber.e(e);
        }
    }

    @Override
    public boolean isFirstLogin() {
        return getPreferenceHelper().isFirstLogin();
    }
}
