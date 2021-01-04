package com.samagra.parent.ui.submissions;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samagra.grove.logging.Grove;
import com.samagra.parent.R;
import com.samagra.parent.data.models.GetPDFListener;
import com.samagra.parent.data.models.PDFItem;
import com.samagra.parent.helper.FetchUserSubmittedPDFModel;
import com.samagra.parent.base.BasePresenter;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static org.odk.collect.android.utilities.ApplicationConstants.SortingOrder.BY_DATE_ASC;
import static org.odk.collect.android.utilities.ApplicationConstants.SortingOrder.BY_DATE_DESC;
import static org.odk.collect.android.utilities.ApplicationConstants.SortingOrder.BY_NAME_ASC;
import static org.odk.collect.android.utilities.ApplicationConstants.SortingOrder.BY_NAME_DESC;

/**
 * The Presenter class for MyVisits Screen. This class controls interaction between the View and Data.
 * This class <b>must</b> implement the {@link SubmissionsMvpPresenter} and <b>must</b> be a type of
 * {@link BasePresenter}.
 *
 * @author Chakshu Gautam
 */
public class SubmissionsPresenter implements SubmissionsMvpPresenter, GetPDFListener {

    private SubmissionsMvpView submissionsMvpView;

    SubmissionsPresenter(SubmissionsMvpView submissionsMvpView) {
        this.submissionsMvpView = submissionsMvpView;
    }

    private int API_FAILURE_MAX_COUNT = 2;
    private int currentAPIRetryCount = 0;
    boolean testing = true;

    @Override
    public void getCachedData() {
        ArrayList<PDFItem> cachedData = getSubmissionsFromCache();

        if (isOldDataPresent(cachedData)) {
            if (isValidSubmission(cachedData)) {
                submissionsMvpView.render(cachedData);
                if (isNetworkConnected()) {
                    fetchSubmissionData();
                }
            } else {
                if(isNetworkConnected()) {
                    fetchSubmissionData();
                    submissionsMvpView.showLoadingView();
                }
                else {
                    submissionsMvpView.onInternetNotConnected();
                }

            }
        } else {
            if (isNetworkConnected()) {
                submissionsMvpView.showLoadingView();
                fetchSubmissionData();
            } else {
                submissionsMvpView.onInternetNotConnected();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public ArrayList<PDFItem> getSubmissionsFromCache() {
        submissionsMvpView.showLoadingView();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(submissionsMvpView.getActivityContext());
        String data = sharedPreferences.getString("submissions", "[]");
        Type listType = new TypeToken<ArrayList<PDFItem>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(data, listType);
    }

    @Override
    public boolean isOldDataPresent(ArrayList<PDFItem> data) {
        return data!= null &&  data.size() > 0;
    }


    @Override
    public boolean isValidSubmission(ArrayList<PDFItem> data) {
        return data!= null && data.size() > 0;
    }

    @Override
    public void fetchSubmissionData() {
        performGetUserSubmissionCall(submissionsMvpView.getUserName());
    }

    @Override
    public boolean isDataUpdated() {
        return true;
    }

    @Override
    public void updateCache(ArrayList<PDFItem> submissions) {
        try {
            Gson gson = new Gson();
            String qq= gson.toJson(submissions);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(submissionsMvpView.getActivityContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("submissions", qq);
            editor.apply();
        } catch ( Exception e) {
        }
    }

    @Override
    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) submissionsMvpView
                .getActivityContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void performGetUserSubmissionCall(String userId) {
        currentAPIRetryCount++;
        new FetchUserSubmittedPDFModel(this, userId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private ArrayList<PDFItem> applyFilters(String filterText, Integer selectedSortingOrder, Integer selectedFormFilter) {
        ArrayList<PDFItem> unfilteredSubmissions = getSubmissionsFromCache();
        ArrayList<PDFItem> submissions = new ArrayList<>();
        if (!filterText.equals("")) {
            for (PDFItem sub : unfilteredSubmissions) {
                if (sub.getPDFItemTags().getFormName().toLowerCase().contains(filterText.toLowerCase())) submissions.add(sub);
            }
        } else {
            submissions = unfilteredSubmissions;
        }

        if(selectedFormFilter == Integer.MAX_VALUE)
            return submissions;

        // Filtering by formName
        ArrayList<String> formOptions = getFormOptions();
        if (!(selectedFormFilter == 0)) {
            ArrayList<PDFItem> submissionsCopy = new ArrayList<>();
            for (PDFItem sub : submissions) {
                if (sub.getPDFItemTags().getFormName().equals(formOptions.get(selectedFormFilter))) {
                    submissionsCopy.add(sub);
                }
            }
            submissions = submissionsCopy;
        }


        switch (selectedSortingOrder) {
            case BY_NAME_ASC:
                Collections.sort(submissions, (o1, o2) -> {
                    int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getPDFItemTags().getFormName(), o2.getPDFItemTags().getFormName());
                    if (res == 0) {
                        res = o1.getPDFItemTags().getFormName().compareTo(o2.getPDFItemTags().getFormName());
                    }
                    return res;
                });
                return submissions;

            case BY_NAME_DESC:
                Collections.sort(submissions, (o1, o2) -> {
                    int res = String.CASE_INSENSITIVE_ORDER.compare(o2.getPDFItemTags().getFormName(), o1.getPDFItemTags().getFormName());
                    if (res == 0) {
                        res = o2.getPDFItemTags().getFormName().compareTo(o1.getPDFItemTags().getFormName());
                    }
                    return res;
                });
                return submissions;

            case BY_DATE_DESC:
                Collections.sort(submissions, new Comparator<PDFItem>() {
                    @Override
                    public int compare(PDFItem o1, PDFItem o2) {
                        return o2.getPDFItemTags().getFormSubmissionDate().compareTo(o1.getPDFItemTags().getFormSubmissionDate());
                    }
                });
                return submissions;

            case BY_DATE_ASC:
                Collections.sort(submissions, new Comparator<PDFItem>() {
                    @Override
                    public int compare(PDFItem o1, PDFItem o2) {
                        return o1.getPDFItemTags().getFormSubmissionDate().compareTo(o2.getPDFItemTags().getFormSubmissionDate());
                    }
                });
                return submissions;

        }
        return submissions;
    }

    void updateFilters(String filterText, Integer selectedSortingOrder, Integer selectedFormFilter) {
        ArrayList<PDFItem> submissions = applyFilters(filterText, selectedSortingOrder, selectedFormFilter);
        submissionsMvpView.render(submissions);

    }

    ArrayList<String> getFormOptions() {
        ArrayList<PDFItem> submissions = getSubmissionsFromCache();
        ArrayList<String> formNames = new ArrayList<>();
        for (PDFItem submission : submissions) {
            formNames.add((submission.getPDFItemTags().getFormName()));
        }
        formNames.add(0, "All");
        return new ArrayList<>(formNames);
    }

    @Override
    public void onSuccess(ArrayList<PDFItem> submissions) {
        Grove.d("Response received from Submissions API, with size of submissions list %s", submissions.size());
        if (!isValidSubmission(submissions)) {
            updateCache(submissions);
            submissionsMvpView.renderNoData();
        }else{
            updateCache(submissions);
            submissionsMvpView.render(submissions);
        }
    }

    @Override
    public void onFailure(Exception exception, FetchUserSubmittedPDFModel.FailureType failureType) {
        if (currentAPIRetryCount < API_FAILURE_MAX_COUNT) {
            Grove.e("Request to Submissions API failed, trying again...");
            getCachedData();
        }
        else {
            Grove.e("Error received from Submissions, Max try count reached already..");
            ArrayList<PDFItem> cachedSubmissions = getSubmissionsFromCache();
            submissionsMvpView.showMessage(submissionsMvpView.getActivityContext().getResources().getString(R.string.error_submission_api));
            if (isValidSubmission(cachedSubmissions))
                submissionsMvpView.render(cachedSubmissions);
            else submissionsMvpView.renderNoData();
        }
    }
}
