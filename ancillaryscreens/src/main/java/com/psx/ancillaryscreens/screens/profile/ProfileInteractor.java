package com.psx.ancillaryscreens.screens.profile;

import com.psx.ancillaryscreens.base.BaseInteractor;
import com.psx.ancillaryscreens.data.prefs.CommonsPreferenceHelper;

import javax.inject.Inject;

public class ProfileInteractor extends BaseInteractor implements ProfileContract.Interactor {

    @Inject
    public ProfileInteractor(CommonsPreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }

    @Override
    public void updateContentKeyInSharedPrefs(String key, String value) {
        getPreferenceHelper().updateProfileKeyValuePair(key, value);
    }

    @Override
    public String getActualContentValue(String key) {
        return getPreferenceHelper().getProfileContentValueForKey(key);
    }
}
