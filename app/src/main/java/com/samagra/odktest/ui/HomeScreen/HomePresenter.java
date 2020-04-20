package com.samagra.odktest.ui.HomeScreen;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.samagra.commons.utils.FormDownloadStatus;
import com.samagra.odktest.MyApplication;
import com.samagra.odktest.R;
import com.samagra.odktest.base.BasePresenter;
import com.samagra.odktest.ui.SearchActivity.SearchActivity;
import com.samagra.odktest.ui.Submissions.SubmissionsActivity;
import com.samagra.user_profile.models.UserProfileElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.activities.FormChooserList;
import org.odk.collect.android.activities.WebViewActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.contracts.DataFormDownloadResultCallback;
import org.odk.collect.android.contracts.FormListDownloadResultCallback;
import org.odk.collect.android.contracts.IFormManagementContract;
import org.odk.collect.android.dto.Form;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.utilities.CustomTabHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * The Presenter class for Home Screen. This class controls interaction between the View and Data.
 * This class <b>must</b> implement the {@link HomeMvpPresenter} and <b>must</b> be a type of {@link BasePresenter}.
 *
 * @author Pranav Sharma
 */
public class HomePresenter<V extends HomeMvpView, I extends HomeMvpInteractor> extends BasePresenter<V, I> implements HomeMvpPresenter<V, I> {




    private FormDownloadStatus formsDownloadStatus = FormDownloadStatus.FAILURE;


    /**
     * The injected values is provided through {@link com.samagra.odktest.di.modules.ActivityAbstractProviders}
     */
    @Inject
    public HomePresenter(I mvpInteractor, IFormManagementContract iFormManagementContract) {
        super(mvpInteractor, iFormManagementContract);
    }


    @Override
    public void onMyVisitClicked(View v) {
        launchActivity(SubmissionsActivity.class);
//        launchActivity(MyVisitsActivity.class);
    }

    @Override
    public void onInspectSchoolClicked(View v) {

        if (formsDownloadStatus.equals(FormDownloadStatus.SUCCESS)) {
            Intent i = new Intent(getMvpView().getActivityContext().getApplicationContext(),
                    FormChooserList.class);
            i.putExtras(getSearchBundle());
            getMvpView().goToForms(i);
        }else{
            getMvpView().showFormsStillDownloading();
        }
    }

    @Override
    public void onSubmitFormClicked(View v) {
        launchActivity(SubmissionsActivity.class);
        // ODKDriver.launchInstanceUploaderListActivity(getMvpView().getActivityContext());
    }

    @Override
    public void onViewIssuesClicked(View v) {
        if (Collect.allowClick(HomeActivity.class.getName())) {
            Intent intent = new Intent(getMvpView().getActivityContext(), WebViewActivity.class);
            intent.putExtra(CustomTabHelper.OPEN_URL, "http://139.59.71.154:3000/public/dashboard/b5bab1e2-7e46-4134-b065-7d62cc4d70d0");
            getMvpView().getActivityContext().startActivity(intent);
        }
    }

    @Override
    public void onHelplineButtonClicked(View v) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:9673464857"));
        v.getContext().startActivity(callIntent);
    }

    @Override
    public void setWelcomeText() {
        getMvpView().updateWelcomeText(getMvpInteractor().getUserName());
    }


    @Override
    public void applySettings() {
        getIFormManagementContract().applyODKCollectSettings(getMvpView().getActivityContext(), R.raw.settings);

    }

    void goToSearch(Bundle searchBundle) {
        Intent intent = new Intent(getMvpView().getActivityContext(), SearchActivity.class);
        intent.putExtras(searchBundle);
        launchActivity(SearchActivity.class, intent);
    }

    @Override
    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getMvpView()
                .getActivityContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
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

    private void startSearchActivity(Class<?> cls) {
        Intent intent = new Intent(getMvpView().getActivityContext(), cls);
        Bundle bundle = new Bundle();
        List<Form> formsFromDB = getIFormManagementContract().getDownloadedFormsNamesFromDatabase();
        HashMap<String, String> formsToBeAutoFilled = new HashMap<>();
        for (Form form : formsFromDB) {
            formsToBeAutoFilled.put(form.getJrFormId(), form.getFormFilePath());
        }
        bundle.putSerializable("forms", formsToBeAutoFilled);
        intent.putExtras(bundle);
        launchActivity(cls, intent);
    }

    private Bundle getSearchBundle(){
        Bundle bundle = new Bundle();
        List<Form> formsFromDB = getIFormManagementContract().getDownloadedFormsNamesFromDatabase();
        HashMap<String, String> formsToBeAutoFilled = new HashMap<>();
        for (Form form : formsFromDB) {
            formsToBeAutoFilled.put(form.getJrFormId(), form.getFormFilePath());
        }
        bundle.putSerializable("forms", formsToBeAutoFilled);
        return bundle;
    }


    private String getUserRoleFromPref() {
        return getMvpInteractor().getPreferenceHelper().getUserRoleFromPref();
    }

    private String getRoleFromRoleMappingFirebase(String userRole) {
        if (userRole.equals("")) return "All";
        class RoleMapping {
            private String Directorate;
            private String Designation;
            private String Role;

            private RoleMapping(String Directorate, String Designation, String Role) {
                this.Directorate = Directorate;
                this.Designation = Designation;
                this.Role = Role;
            }
        }

        String roleMapping = MyApplication.getmFirebaseRemoteConfig().getString("role_mapping");
        Timber.e("Role Mapping :: ");
        Timber.e(roleMapping);
        String role = "";
        ArrayList<RoleMapping> roleMappings = new ArrayList<>();
        if (!roleMapping.equals("") && !userRole.equals("")) {
            try {
                boolean found = false;
                JSONArray jsonArray = new JSONArray(roleMapping);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String directorate = null;
                    if (object.has("Directorate")) directorate = object.getString("Directorate");
                    roleMappings.add(new RoleMapping(
                            directorate,
                            object.getString("Designation"),
                            object.getString("Role"))
                    );
                    if (roleMappings.get(i).Designation.equals(userRole)) {
                        found = true;
                        role = roleMappings.get(i).Role;
                    }
                }
                if (!found) role = "All";
            } catch (JSONException e) {
                e.printStackTrace();
                role = "All";
            }
        }
        return role;
    }

    void checkForFormUpdates() {
        String latestFormVrsion = MyApplication.getmFirebaseRemoteConfig().getString("version");
        String previousVersion = getMvpInteractor().getPreferenceHelper().getFormVersion();
        String formsString = MyApplication.getmFirebaseRemoteConfig().getString(getRoleFromRoleMappingFirebase(getUserRoleFromPref()));
        formsDownloadStatus = FormDownloadStatus.DOWNLOADING;
        Timber.e("Are forms matching: %s", getIFormManagementContract().checkIfODKFormsMatch(formsString));
        if (isUpversioned(latestFormVrsion, previousVersion) || !getIFormManagementContract().checkIfODKFormsMatch(formsString)) {
            getMvpInteractor().getPreferenceHelper().updateFormVersion(latestFormVrsion);
            // Downloading new forms list.
            getIFormManagementContract().startDownloadODKFormListTask(new FormListDownloadListener());
            formsDownloadStatus = FormDownloadStatus.DOWNLOADING;
        } else {
            if (getMvpView() != null){
                Timber.d("Rendering UI Visible as forms already downloaded");
                getMvpView().showSnackbar("Forms have already been downloaded.", Snackbar.LENGTH_LONG);
                getMvpView().renderLayoutVisible();}
            formsDownloadStatus = FormDownloadStatus.SUCCESS;
        }
    }

    private boolean isUpversioned(String version, String previousVersion) {
        try {
            return Integer.parseInt(version) > Integer.parseInt(previousVersion);
        } catch (Exception e) {
            return false;
        }
    }




    ArrayList<UserProfileElement> getProfileConfig() {
        String configString = MyApplication.getmFirebaseRemoteConfig().getString("profile_config");
        ArrayList<UserProfileElement> userProfileElements = new ArrayList<>();

        try {
            JSONArray config = new JSONArray(configString);
            for(int i=0; i<config.length(); i++){
                JSONArray spinnerExtra = config.getJSONObject(i).optJSONArray("spinnerExtra");
                ArrayList<String> spinnerValues = null;
                if(spinnerExtra != null){
                    for(int j=0; j<spinnerExtra.length(); j++){
                        spinnerValues.add(spinnerExtra.get(j).toString());
                    }
                }

                userProfileElements.add(new UserProfileElement(config.getJSONObject(i).get("base64Icon").toString(),
                        config.getJSONObject(i).get("title").toString(),
                        config.getJSONObject(i).get("content").toString(),
                        (Boolean) config.getJSONObject(i).get("isEditable"),
                        (int) config.getJSONObject(i).get("section"),
                        UserProfileElement.ProfileElementContentType.valueOf(config.getJSONObject(i).get("type").toString()),
                        spinnerValues));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userProfileElements;
    }

    class FormListDownloadListener implements FormListDownloadResultCallback {
        @Override
        public void onSuccessfulFormListDownload(HashMap<String, FormDetails> latestFormListFromServer) {
            Timber.d("FormList download complete %s", latestFormListFromServer);
            String formsString = MyApplication.getmFirebaseRemoteConfig().getString(getRoleFromRoleMappingFirebase(getUserRoleFromPref()));
            HashMap<String, String> userRoleBasedForms = getIFormManagementContract().downloadFormList(formsString);
            // Download Forms if updates available or if forms not downloaded. Delete forms if not applied for the role.
            HashMap<String, String> formsToBeDownloaded = getIFormManagementContract().downloadNewFormsBasedOnDownloadedFormList(userRoleBasedForms, latestFormListFromServer);
            if (formsToBeDownloaded.size() > 0)
                formsDownloadStatus = FormDownloadStatus.DOWNLOADING;
            else {
                formsDownloadStatus = FormDownloadStatus.SUCCESS;
            }
            if (formsDownloadStatus == FormDownloadStatus.DOWNLOADING)
                getIFormManagementContract().downloadODKForms(new FormDownloadListener(), formsToBeDownloaded);
        }
        @Override
        public void onFailureFormListDownload(boolean isAPIFailure) {
            if (isAPIFailure) {
                Timber.e("There has been an error in downlaoding the forms from Aggregagte");
                getMvpView().showSnackbar("There has been an error in downlaoding the forms from Aggregagte. \n" +
                        "Please check if URL is valid or not and ODK configs are alright.", Snackbar.LENGTH_LONG);
                getMvpView().renderLayoutVisible();
                formsDownloadStatus = FormDownloadStatus.FAILURE;
            }
            //+ Show error Message
//            checkForFormUpdates();
        }
    }


    class FormDownloadListener implements DataFormDownloadResultCallback {
        @Override
        public void formsDownloadingSuccessful(HashMap<FormDetails, String> result) {
            Timber.d("Form Download Complete %s", result);
            formsDownloadStatus = FormDownloadStatus.SUCCESS;
            if (getMvpView() != null)
                getMvpView().renderLayoutVisible();
        }

        @Override
        public void formsDownloadingFailure() {

        }

        @Override
        public void progressUpdate(String currentFile, int progress, int total) {
            Timber.v("Form Download InProgress = " + currentFile + " Progress" + progress + " Out of=" + total);
            Timber.d(String.valueOf(total));
            Timber.d(String.valueOf(progress));
            int formProgress = (progress * 100) / total;
            Timber.d("Form Download Progress: %s", formProgress);
            if (formProgress == 100) {
                if (getMvpView() != null) {
                    Timber.d("Rendering UI Visible as forms already downloadded not, but now downloaded");
                    getMvpView().renderLayoutVisible();
                    getMvpView().showSnackbar(       "ODK forms as requested have been downloaded.", Snackbar.LENGTH_LONG);
                }
                formsDownloadStatus = FormDownloadStatus.SUCCESS;
            }
        }

        @Override
        public void formsDownloadingCancelled() {
            getMvpView().showSnackbar("Unable to download the forms.", Snackbar.LENGTH_LONG);
            Timber.e("Form Download Cancelled");
        }
    }

}
