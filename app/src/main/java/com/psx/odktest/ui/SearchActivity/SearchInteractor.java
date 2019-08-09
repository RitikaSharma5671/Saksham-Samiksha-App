package com.psx.odktest.ui.SearchActivity;

import com.psx.odktest.base.BaseInteractor;
import com.psx.odktest.data.prefs.PreferenceHelper;

import javax.inject.Inject;

/**
 * This class interacts with the {@link SearchMvpPresenter} and the stored app data. The class abstracts
 * the source of the originating data - This means {@link SearchMvpPresenter} has no idea if the data provided
 * by the {@link SearchInteractor} is from network, database or SharedPreferences
 * This class <b>must</b> implement {@link SearchMvpPresenter} and <b>must</b> extend {@link BaseInteractor}.
 *
 * @author Pranav Sharma
 */
public class SearchInteractor extends BaseInteractor implements SearchMvpInteractor {

    @Inject
    public SearchInteractor(PreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }
}
