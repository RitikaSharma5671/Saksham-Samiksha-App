package com.psx.odktest.ui.HomeScreen;

import com.psx.odktest.base.BaseInteractor;
import com.psx.odktest.data.prefs.PreferenceHelper;

import javax.inject.Inject;

public class HomeInteractor extends BaseInteractor implements HomeMvpInteractor {

    @Inject
    public HomeInteractor(PreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }

    @Override
    public String getUserName() {
        return getPreferenceHelper().getCurrentUserName();
    }
}
