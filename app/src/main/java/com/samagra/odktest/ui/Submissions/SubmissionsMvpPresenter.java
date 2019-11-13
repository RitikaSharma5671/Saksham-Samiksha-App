package com.samagra.odktest.ui.Submissions;

import android.view.View;

import com.samagra.odktest.base.MvpPresenter;
import com.samagra.odktest.data.models.Submission;
import com.samagra.odktest.di.PerActivity;

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
public interface SubmissionsMvpPresenter<V extends SubmissionsMvpView, I extends SubmissionsMvpInteractor> extends MvpPresenter<V, I> {

    void getCachedData();

    boolean isOldDataPresent(ArrayList<Submission> data);

    boolean isValidSubmission(ArrayList<Submission> data);

    void fetchSubmissionData();

    boolean isDataUpdated();

    void updateCache(String submissions);

    boolean isNetworkConnected();

}
    

