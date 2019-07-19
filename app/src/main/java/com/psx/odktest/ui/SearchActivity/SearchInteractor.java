package com.psx.odktest.ui.SearchActivity;

import com.psx.odktest.base.BaseInteractor;
import com.psx.odktest.data.prefs.PreferenceHelper;

import javax.inject.Inject;

public class SearchInteractor extends BaseInteractor implements SearchMvpInteractor {

    @Inject
    public SearchInteractor(PreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }
}
