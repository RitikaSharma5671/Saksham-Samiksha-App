package com.samagra.parent.ui.submissions;

import com.samagra.parent.base.BaseInteractor;
import com.samagra.parent.data.prefs.PreferenceHelper;

import javax.inject.Inject;

/**
 * This class interacts with the {@link SubmissionsMvpPresenter} and the stored app data. The class
 * abstracts the source of the originating data - This means {@link SubmissionsMvpPresenter} has no idea
 * if the data provided by the {@link SubmissionsInteractor} is from network, database or
 * SharedPreferences. This class <b>must</b> implement {@link SubmissionsMvpInteractor} and extend
 * {@link BaseInteractor}.
 *
 * @author Pranav Sharma
 */
public class SubmissionsInteractor extends BaseInteractor implements SubmissionsMvpInteractor {

    /**
     * This injected value of {@link PreferenceHelper} is provided through
     */
    @Inject
    public SubmissionsInteractor(PreferenceHelper preferenceHelper) {
        super(preferenceHelper);
    }
}
