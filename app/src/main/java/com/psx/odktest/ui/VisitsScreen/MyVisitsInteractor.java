package com.psx.odktest.ui.VisitsScreen;

import com.psx.odktest.base.BaseInteractor;
import com.psx.odktest.data.prefs.PreferenceHelper;

import javax.inject.Inject;

public class MyVisitsInteractor extends BaseInteractor implements MyVisitMvpInteractor {

    @Inject
    public MyVisitsInteractor(PreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }
}
