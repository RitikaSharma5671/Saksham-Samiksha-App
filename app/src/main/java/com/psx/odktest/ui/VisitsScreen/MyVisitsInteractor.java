package com.psx.odktest.ui.VisitsScreen;

import com.psx.odktest.base.BaseInteractor;
import com.psx.odktest.data.prefs.PreferenceHelper;

import javax.inject.Inject;

/**
 * This class interacts with the {@link MyVisitsMvpPresenter} and the stored app data. The class abstracts
 * the source of the originating data - This means {@link MyVisitsMvpPresenter} has no idea if the data provided
 * by the {@link MyVisitsInteractor} is from network, database or SharedPreferences
 * This class <b>must</b> implement {@link MyVisitMvpInteractor} and <b>must</b> extend {@link BaseInteractor}.
 *
 * @author Pranav Sharma
 */
public class MyVisitsInteractor extends BaseInteractor implements MyVisitMvpInteractor {

    /**
     * This injected value of {@link PreferenceHelper} is provided through
     * {@link com.psx.odktest.di.modules.ApplicationModule}
     */
    @Inject
    public MyVisitsInteractor(PreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }
}
