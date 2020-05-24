package com.samagra.parent.ui.submissions;

import com.samagra.parent.data.models.PDFItem;
import com.samagra.parent.di.PerActivity;

import java.util.ArrayList;

/**
 * The Presenter 'contract' for the MyVisits Screen. The @link SubmissionsPresenter <b>must</b> implement
 * this interface. This interface exposes presenter methods to the view (@link SubmissionsActivity); so
 * that the business logic is defined in the presenter, but can be called from the view.
 * This interface should be a type of @link MvpPresenter
 *
 * @author Pranav Sharma
 */
@PerActivity
public interface SubmissionsMvpPresenter {

    void getCachedData();

    boolean isOldDataPresent(ArrayList<PDFItem> data);

    boolean isValidSubmission(ArrayList<PDFItem> data);

    void fetchSubmissionData();

    boolean isDataUpdated();

    void updateCache(ArrayList<PDFItem> submissions);

    boolean isNetworkConnected();

}
    

