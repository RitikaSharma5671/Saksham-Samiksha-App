package com.samagra.parent.ui.submissions;


import com.samagra.parent.base.MvpView;
import com.samagra.parent.data.models.PDFItem;

import java.util.ArrayList;

/**
 * The view interface 'contract' for the MyVisits Screen. This defines all the functionality required
 * by the Presenter for the view as well as for enforcing certain structure in the Views.
 * The {@link SubmissionsActivity} <b>must</b> implement this interface. This way, the business logic
 * behind the screen can remain unaffected.
 *
 * @author Pranav Sharma
 */
public interface SubmissionsMvpView extends MvpView {

    void onRefreshButtonPressed();

    void showViewLayout();

    void showProgressBar();

    void animateSmallProgressBar();

    void render(ArrayList<PDFItem> submissions);

    void renderNoData();

    void onInternetNotConnected();

    void showMessage(String s);

    void showLoadingView();

    String getUserName();
}
