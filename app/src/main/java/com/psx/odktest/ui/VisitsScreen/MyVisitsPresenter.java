package com.psx.odktest.ui.VisitsScreen;

import android.content.Intent;
import android.view.View;

import com.psx.odktest.base.BasePresenter;
import com.psx.odktest.ui.ComingSoon.ComingSoon;

import org.odk.collect.android.activities.InstanceChooserList;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.preferences.GeneralKeys;
import org.odk.collect.android.utilities.ApplicationConstants;

import javax.inject.Inject;

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
            i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE,
                    ApplicationConstants.FormModes.VIEW_SENT);
            getMvpView().getActivityContext().startActivity(i);
        }
    }
}
