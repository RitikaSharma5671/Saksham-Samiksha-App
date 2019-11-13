package com.samagra.odktest.ui.Submissions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.View;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rx2androidnetworking.Rx2AndroidNetworking;
import com.samagra.ancillaryscreens.data.network.BackendCallHelperImpl;
import com.samagra.ancillaryscreens.data.network.model.LoginRequest;
import com.samagra.ancillaryscreens.data.network.model.LoginResponse;
import com.samagra.ancillaryscreens.screens.login.LoginPresenter;
import com.samagra.commons.Constants;
import com.samagra.odktest.R;
import com.samagra.odktest.UtilityFunctions;
import com.samagra.odktest.base.BasePresenter;
import com.samagra.odktest.data.models.Submission;
import com.samagra.odktest.ui.ComingSoon.ComingSoon;
import com.samagra.odktest.ui.HomeScreen.HomeActivity;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.odk.collect.android.activities.InstanceChooserList;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.preferences.GeneralKeys;
import org.odk.collect.android.utilities.ApplicationConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * The Presenter class for MyVisits Screen. This class controls interaction between the View and Data.
 * This class <b>must</b> implement the {@link SubmissionsMvpPresenter} and <b>must</b> be a type of
 * {@link BasePresenter}.
 *
 * @author Chakshu Gautam
 */
public class SubmissionsPresenter<V extends SubmissionsMvpView, I extends SubmissionsMvpInteractor>
        extends BasePresenter<V, I> implements SubmissionsMvpPresenter<V, I> {

    @Inject
    public SubmissionsPresenter(I mvpInteractor) {
        super(mvpInteractor);
    }


    @Override
    public void getCachedData() {
        ArrayList<Submission> cachedData = getSubmissionsFromCache();

        if (isOldDataPresent(cachedData)) {
            if (isValidSubmission(cachedData)) {
                getMvpView().render(cachedData);
                if (isNetworkConnected()) {
                    fetchSubmissionData();
                }
            } else {
                getMvpView().renderNoData();
            }
        } else {
            if (isNetworkConnected()) {
                fetchSubmissionData();
            } else {
                // Show not connected to internet bar.
            }
        }
    }

    @Nullable
    public ArrayList<Submission> getSubmissionsFromCache() {
        getMvpView().showProgressBar();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getMvpView().getActivityContext());
        String data = sharedPreferences.getString("submissions", "[]");
        Type listType = new TypeToken<ArrayList<Submission>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(data, listType);
    }

    @Override
    public boolean isOldDataPresent(ArrayList<Submission> data) {
        return true;
    }


    @Override
    public boolean isValidSubmission(ArrayList<Submission> data) {
        return true;
    }

    @Override
    public void fetchSubmissionData() {
        performGetUserDetailsApiCall("test", "test");
    }

    @Override
    public boolean isDataUpdated() {
        return true;
    }

    @Override
    public void updateCache(String submissions) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getMvpView().getActivityContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("submissions", submissions);
        editor.apply();
    }

    @Override
    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getMvpView()
                .getActivityContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void performGetUserDetailsApiCall(String userId, String applicationID) {
        String url = "https://gist.githubusercontent.com/Rishabh04-021/57eabb1557181751579da8ea48ad52fb/raw/fe8c187d336dbfd7a10ae80d4c6b7ee7cef2db90/one-completed-other-not.json";
        Rx2AndroidNetworking.get(url)
                .addPathParameter("userId", userId)
                .addPathParameter("applicationId", applicationID)
                .addHeaders("Authorization", "sample")
                .setTag(Constants.LOGOUT_CALLS)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Type listType = new TypeToken<ArrayList<Submission>>() {
                        }.getType();
                        Gson gson = new Gson();
                        ArrayList<Submission> submissions = gson.fromJson(response.toString(), listType);
                        if (isDataUpdated()) {
                            updateCache(response.toString());
                            getMvpView().render(submissions);
                        } else {
                            if (!isValidSubmission(submissions)) {
                                getMvpView().hideProgressBar();
                                getMvpView().renderNoData();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        getCachedData();
                    }

                });
    }

    private void launchActivity(Class clazz) {
        if (Collect.allowClick(HomeActivity.class.getName())) {
            Intent intent = new Intent(getMvpView().getActivityContext(), clazz);
            getMvpView().getActivityContext().startActivity(intent);
        }
    }

    private void launchActivity(Class clazz, Intent intent) {
        if (Collect.allowClick(HomeActivity.class.getName())) {
            getMvpView().getActivityContext().startActivity(intent);
        }
    }

}
