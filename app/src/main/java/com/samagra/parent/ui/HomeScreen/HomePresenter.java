package com.samagra.parent.ui.HomeScreen;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.samagra.commons.InstitutionInfo;
import com.samagra.commons.utils.FormDownloadStatus;
import com.samagra.grove.logging.Grove;
import com.samagra.parent.MyApplication;
import com.samagra.parent.R;
import com.samagra.parent.UtilityFunctions;
import com.samagra.parent.base.BasePresenter;
import com.samagra.parent.ui.submissions.SubmissionsActivity;
import com.samagra.user_profile.profile.UserProfileElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.contracts.DataFormDownloadResultCallback;
import org.odk.collect.android.contracts.FormListDownloadResultCallback;
import org.odk.collect.android.contracts.IFormManagementContract;
import org.odk.collect.android.dto.Form;
import org.odk.collect.android.logic.FormDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;


/**
 * The Presenter class for Home Screen. This class controls interaction between the View and Data.
 * This class <b>must</b> implement the {@link HomeMvpPresenter} and <b>must</b> be a type of {@link BasePresenter}.
 *
 * @author Pranav Sharma
 */
public class HomePresenter<V extends HomeMvpView, I extends HomeMvpInteractor> extends BasePresenter<V, I> implements HomeMvpPresenter<V, I> {

    private FormDownloadStatus formsDownloadStatus = FormDownloadStatus.FAILURE;
    private int currentProgress = 0;
    private int maxProgress = 10;

    @Inject
    public HomePresenter(I mvpInteractor, IFormManagementContract iFormManagementContract) {
        super(mvpInteractor, iFormManagementContract);
    }

    @Override
    public void onFillFormsOptionClicked() {
        getIFormManagementContract().launchFormChooserView(getMvpView().getActivityContext(), UtilityFunctions.generateToolbarModificationObject(true,
                R.drawable.ic_arrow_back_white_24dp, getMvpView().getActivityContext().getResources().getString(R.string.please_select_forms), true));
    }

    @Override
    public void fetchWelcomeText() {
        Grove.d("Fetching welcome text for the user...");
        getMvpView().displayHomeWelcomeText(getMvpInteractor().getUserName());
    }

    @Override
    public void onViewSubmittedFormsOptionsClicked() {
        Grove.d("User selects the option View Submitted Forms...");

        if (getMvpView() != null) {

            Intent intent = new Intent(getMvpView().getActivityContext(), SubmissionsActivity.class);
            getMvpView().getActivityContext().startActivity(intent);
           }
    }


    @Override
    public void onSubmitFormsClicked() {
        Grove.d("User selects the option Submit Forms...");
        if (getMvpView() != null) {
            getIFormManagementContract().launchViewUnsubmittedFormView(getMvpView().getActivityContext(), getClass().getName(), UtilityFunctions.generateToolbarModificationObject(true,
                    R.drawable.ic_arrow_back_white_24dp,
                    getMvpView().getActivityContext().getResources().getString(R.string.submit_saved_forms), true));
        }
    }

    @Override
    public void onViewHelplineClicked() {
        Grove.d("User selects the option View Helpline...");
        if (getMvpView() != null) {
            Intent i = new Intent(getMvpView().getActivityContext(), ComingSoon.class);
            getMvpView().getActivityContext().startActivity(i);
        }
    }


    @Override
    public boolean isNetworkConnected() {
        if (getMvpView() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getMvpView()
                    .getActivityContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        } else {
            return MyApplication.isOnline;
        }
    }

    @Override
    public String getYoutubeAPIKey() {
        String youtubeAPIKey = "";
        if (MyApplication.getmFirebaseRemoteConfig() != null) {
            youtubeAPIKey = MyApplication.getmFirebaseRemoteConfig().getString("youtube_api_key");
        }
        return youtubeAPIKey;
    }

    @Override
    public String getTutorialVideoID() {
        String youtubeTutorialVideoID = "";
        if (MyApplication.getmFirebaseRemoteConfig() != null) {
            youtubeTutorialVideoID = MyApplication.getmFirebaseRemoteConfig().getString("youtube_tutorial_video_id");
        }
        return youtubeTutorialVideoID;
    }

    @Override
    public void applySettings() {
        getIFormManagementContract().applyODKCollectSettings(getMvpView().getActivityContext(), R.raw.settings);
    }


    /**
     * Check if the ODK Forms have been updated?
     * @param  version New Form Version
     * @param  previousVersion Old Form Version from preferences
     */
    private boolean isUpversioned(String version, String previousVersion) {
        try {
            return Integer.parseInt(version) > Integer.parseInt(previousVersion);
        } catch (Exception e) {
            return false;
        }
    }


    private String getUserRoleFromPref() {
        return getMvpInteractor().getPreferenceHelper().getUserRoleFromPref();
        //Viewing and download of forms is based on User's role, you can configure it via Preferences when logging in as per User's Login response
    }


    private String getRoleFromRoleMappingFirebase(String userRole) {
        if (userRole.equals("")) return "all_grades";
        class RoleMapping {
            private String Designation;
            private String Role;

            private RoleMapping(String Designation, String Role) {
                this.Designation = Designation;
                this.Role = Role;
            }
        }


        String roleMapping = MyApplication.getmFirebaseRemoteConfig().getString("role_mapping");
        Grove.d("Finding the Role Mapping for the user");
        Grove.e("Role Mapping string from firebase  is %s", roleMapping);
        String role = "";
        ArrayList<RoleMapping> roleMappings = new ArrayList<>();
        if (!roleMapping.equals("") && !userRole.equals("")) {
            try {
                boolean found = false;
                JSONArray jsonArray = new JSONArray(roleMapping);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    roleMappings.add(new RoleMapping(
                            object.getString("Designation"),
                            object.getString("Role"))
                    );
                    if (roleMappings.get(i).Designation.equals(userRole)) {
                        found = true;
                        Grove.d("Role for the user from Role Mapping is %s", userRole);
                        role = roleMappings.get(i).Role;
                    }
                }
                if (!found) {
                    Grove.d("No mapping found with respect to the account preferences");
                    role = "All";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                role = "All";
            }
        }
        return role;
    }

    @Override
    public void checkForFormUpdates() {
        Grove.d("Checking for form updates");
        getMvpView().renderLayoutInvisible();
        String latestFormVrsion = MyApplication.getmFirebaseRemoteConfig().getString("version");
        String previousVersion = getMvpInteractor().getPreferenceHelper().getFormVersion();
        String formsString = MyApplication.getmFirebaseRemoteConfig().getString(getRoleFromRoleMappingFirebase(getUserRoleFromPref()));
        formsDownloadStatus = FormDownloadStatus.DOWNLOADING;
        Grove.e("Checking if the forms are matching: %s", getIFormManagementContract().checkIfODKFormsMatch(formsString));
        if (isNetworkConnected()) {
            if (isUpversioned(latestFormVrsion, previousVersion) ||
                    !getIFormManagementContract().checkIfODKFormsMatch(formsString)) {
                Grove.d("Forms have been up-versioned/ odk forms don't match with one needed for the user...");
                getMvpInteractor().getPreferenceHelper().updateFormVersion(latestFormVrsion);
                // Downloading new forms list.
                getMvpView().setDownloadProgress(10);
                Grove.d("Starting Form List Download Task");
                getIFormManagementContract().startDownloadODKFormListTask(new FormListDownloadListener());
                formsDownloadStatus = FormDownloadStatus.DOWNLOADING;
            } else {
                Grove.d("Network Available, forms are matching, rendering the layout");
                if (getMvpView() != null) {
                    Grove.d("Rendering UI Visible as forms already downloaded");
                    getMvpView().renderLayoutVisible();
                }
                formsDownloadStatus = FormDownloadStatus.SUCCESS;
            }
        } else {
            Grove.d("Network not available, can't download forms/ check versions, rendering layou");
            if (getMvpView() != null) {
                Grove.d("Rendering UI Visible as forms already downloaded");
                getMvpView().renderLayoutVisible();
                getMvpView().showNoInternetMessage();
            }
        }
    }

    @Override
    public ArrayList<UserProfileElement> getProfileConfig() {
        Grove.d("Fetching the User Profile Params");
        String configString = MyApplication.getmFirebaseRemoteConfig().getString("profileConfig");
        ArrayList<UserProfileElement> userProfileElements = new ArrayList<>();
        try {
            JSONArray config = new JSONArray(configString);
            for (int i = 0; i < config.length(); i++) {
                JSONArray spinnerExtra = config.getJSONObject(i).optJSONArray("spinnerExtra");
                ArrayList<String> spinnerValues = null;
                if (spinnerExtra != null) {
                    spinnerValues = new ArrayList<>();
                    for (int j = 0; j < spinnerExtra.length(); j++) {
                        spinnerValues.add(spinnerExtra.get(j).toString());
                    }
                }
                userProfileElements.add(new UserProfileElement(config.getJSONObject(i).get("base64Icon").toString(),
                        config.getJSONObject(i).get("title").toString(),
                        config.getJSONObject(i).get("content").toString(),
                        (Boolean) config.getJSONObject(i).get("isEditable"),
                        (int) config.getJSONObject(i).get("section"),
                        UserProfileElement.ProfileElementContentType.valueOf(config.getJSONObject(i).get("type").toString()),
                        spinnerValues,
                        getMvpInteractor().getPreferenceHelper().getValueForKey(config.getJSONObject(i).get("content").toString())
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userProfileElements;
    }

    String fetchUserID() {
        return getMvpInteractor().getPreferenceHelper().getCurrentUserId();
    }


    @Override
    public void updateLanguageSettings() {
        String language = getMvpInteractor().getPreferenceHelper().updateAppLanguage();
        getMvpView().updateLocale(language);
    }

    @Override
    public void resetODKData() {
        getIFormManagementContract().resetEverythingODK();
    }

    @Override
    public void resetProgressVariables() {
        currentProgress = 0;
        maxProgress = 10;
    }

    @Override
    public void prefillData(InstitutionInfo institutionInfo) {
        Grove.d("Prefilling data for the Forms downloaded");
        List<Form> forms = getIFormManagementContract().getDownloadedFormsNamesFromDatabase();
        for (Form form : forms) {
            String formName = form.getDisplayName();
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "district", institutionInfo.District);
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "block", institutionInfo.Block);
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "school", institutionInfo.SchoolName);
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "username", getMvpInteractor().getUserName());
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "name", getMvpInteractor().getUserName());
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "designation", getUserRoleFromPref());
        }
    }

    @Override
    public void searchmodule() {
        getMvpView().launchSearchModule();
    }

    class FormListDownloadListener implements FormListDownloadResultCallback {
        @Override
        public void onSuccessfulFormListDownload(HashMap<String, FormDetails> latestFormListFromServer) {
            Grove.d("FormList download complete %s, is the form list size", latestFormListFromServer.size());
            String formsString = MyApplication.getmFirebaseRemoteConfig().getString(getRoleFromRoleMappingFirebase(getUserRoleFromPref()));
            HashMap<String, String> userRoleBasedForms = getIFormManagementContract().downloadFormList(formsString);
            // Download Forms if updates available or if forms not downloaded. Delete forms if not applied for the role.
            HashMap<String, FormDetails> formsToBeDownloaded = getIFormManagementContract().downloadNewFormsBasedOnDownloadedFormList(userRoleBasedForms, latestFormListFromServer);
            if (formsToBeDownloaded.size() > 0) {
                Grove.d("Number of forms to be downloaded are %d", formsToBeDownloaded.size());
                formsDownloadStatus = FormDownloadStatus.DOWNLOADING;
                currentProgress = 2;
                getMvpView().setDownloadProgress(30);
                currentProgress = 30;
                maxProgress = formsToBeDownloaded.size();
            } else {
                Grove.d("No new forms to be downloaded");
                getMvpView().setDownloadProgress(100);
                formsDownloadStatus = FormDownloadStatus.SUCCESS;
                getMvpView().renderLayoutVisible();
            }
            if (formsDownloadStatus == FormDownloadStatus.DOWNLOADING)
                getIFormManagementContract().downloadODKForms(new FormDownloadListener(), formsToBeDownloaded);
        }

        @Override
        public void onFailureFormListDownload(boolean isAPIFailure) {
            if (isAPIFailure) {
                Grove.e("There has been an error in downloading the forms from ODK Server");
                getMvpView().showDownloadFailureMessage();
                formsDownloadStatus = FormDownloadStatus.FAILURE;
            }
            getMvpView().renderLayoutVisible();
            if (!isNetworkConnected())
                getMvpView().showFailureDownloadMessage();
            else
                getMvpView().showNoInternetMessage();
        }
    }


    class FormDownloadListener implements DataFormDownloadResultCallback {
        @Override
        public void formsDownloadingSuccessful(HashMap<FormDetails, String> result) {
            Grove.d("Form Download Complete %s", result);
            formsDownloadStatus = FormDownloadStatus.SUCCESS;
            if (getMvpView() != null) {
                getMvpView().renderLayoutVisible();
                getMvpView().setDownloadProgress(100);
            }
        }

        @Override
        public void formsDownloadingFailure() {

        }

        @Override
        public void progressUpdate(String currentFile, int progress, int total) {
            Grove.v("Form Download InProgress = " + currentFile + " Progress" + progress + " Out of=" + total);
            Grove.d(" Total%s", String.valueOf(total));
            Grove.d(" Total Progress %s", String.valueOf(progress));
            int formProgress = (progress * 100) / total;
            Grove.d("Form Download Progress: %s", formProgress);
            currentProgress = currentProgress + 70 / 9;
            getMvpView().setDownloadProgress(currentProgress);
            if (formProgress == 100) {
                if (getMvpView() != null) {
                    currentProgress = 100;
                    getMvpView().setDownloadProgress(currentProgress);
                    Grove.d("Rendering UI Visible as forms already downloaded not, but now downloaded");
                }
                formsDownloadStatus = FormDownloadStatus.SUCCESS;
            }
        }

        @Override
        public void formsDownloadingCancelled() {
            getMvpView().renderLayoutVisible();
            getMvpView().showFailureDownloadMessage();
            Grove.e("Form Download Cancelled >> API Cancelled callback received");
        }
    }


}