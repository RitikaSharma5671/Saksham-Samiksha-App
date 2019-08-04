package com.psx.odktest.ui.VisitsScreen;

import android.content.Intent;
import android.view.View;

import com.psx.commons.Constants;
import com.psx.odktest.R;
import com.psx.odktest.UtilityFunctions;
import com.psx.odktest.base.BasePresenter;
import com.psx.odktest.ui.ComingSoon.ComingSoon;

import org.odk.collect.android.activities.InstanceChooserList;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.preferences.GeneralKeys;
import org.odk.collect.android.utilities.ApplicationConstants;

import javax.inject.Inject;

/**
 * The Presenter class for Home Screen. This class controls interaction between the View and Data.
 * This class <b>must</b> implement the {@link MyVisitsMvpPresenter} and <b>must</b> be a type of {@link BasePresenter}.
 *
 * @author Pranav Sharma
 */
public class MyVisitsPresenter<V extends MyVisitsMvpView, I extends MyVisitMvpInteractor>
        extends BasePresenter<V, I> implements MyVisitsMvpPresenter<V, I> {

    @Inject
    public MyVisitsPresenter(I mvpInteractor) {
        super(mvpInteractor);
    }

    @Override
    public void onViewVisitStatusClicked(View v) {
        if (Collect.allowClick(getClass().getName())) {
            Intent i = new Intent(getMvpView().getActivityContext(), ComingSoon.class);
            i.putExtra(GeneralKeys.TITLE, "My Visits");
            getMvpView().getActivityContext().startActivity(i);
        }
    }

    @Override
    public void onViewSubmittedFormsClicked(View v) {
        if (Collect.allowClick(getClass().getName())) {
            Intent i = new Intent(getMvpView().getActivityContext(), InstanceChooserList.class);
            i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.VIEW_SENT);
            i.putExtra(Constants.KEY_CUSTOMIZE_TOOLBAR, UtilityFunctions.generateToolbarModificationObject(true,
                    R.drawable.ic_arrow_back_white_24dp, "View Submitted Forms", true));
            i.putIntegerArrayListExtra(Constants.CUSTOM_TOOLBAR_ARRAYLIST_HIDE_IDS, null);
            getMvpView().getActivityContext().startActivity(i);
        }
    }
}
