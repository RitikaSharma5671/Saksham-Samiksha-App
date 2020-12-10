package com.samagra.parent.ui.HomeScreen;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.student_details.contracts.ApolloQueryResponseListener;
import com.example.student_details.contracts.EmployeeInfoListener;
import com.example.student_details.contracts.IStudentDetailsContract;
import com.example.student_details.contracts.StudentDetailsComponentManager;
import com.example.student_details.models.realm.SchoolEmployeesInfo;
import com.google.android.material.snackbar.Snackbar;
import com.hasura.model.GetStudentsForSchoolQuery;
import com.hasura.model.SendUsageInfoMutation;
import com.samagra.ancillaryscreens.screens.profile.UserProfileElement;
import com.samagra.commons.InstitutionInfo;
import com.samagra.commons.utils.FormDownloadStatus;
import com.samagra.grove.logging.Grove;
import com.samagra.parent.MyApplication;
import com.samagra.parent.R;
import com.samagra.parent.UtilityFunctions;
import com.samagra.parent.base.BasePresenter;
import com.samagra.parent.helper.BackendNwHelper;
import com.samagra.parent.helper.KeyboardHandler;
import com.samagra.parent.ui.submissions.SubmissionsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.contracts.CSVBuildStatusListener;
import org.odk.collect.android.contracts.CSVHelper;
import org.odk.collect.android.contracts.DataFormDownloadResultCallback;
import org.odk.collect.android.contracts.FormListDownloadResultCallback;
import org.odk.collect.android.contracts.IFormManagementContract;
import org.odk.collect.android.formmanagement.ServerFormDetails;
import org.odk.collect.android.forms.Form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * The Presenter class for Home Screen. This class controls interaction between the View and Data.
 * This class <b>must</b> implement the {@link HomeMvpPresenter} and <b>must</b> be a type of {@link BasePresenter}.
 *
 * @author Pranav Sharma
 */
@SuppressWarnings("ConstantConditions")
public class HomePresenter<V extends HomeMvpView, I extends HomeMvpInteractor> extends BasePresenter<V, I> implements HomeMvpPresenter<V, I> {

    private FormDownloadStatus formsDownloadStatus = FormDownloadStatus.FAILURE;
    private FormDownloadStatus studentDownloadStatus = FormDownloadStatus.FAILURE;
    private int currentProgress = 0;
    private boolean isPermissionGiven = true;

    @Inject
    public HomePresenter(I mvpInteractor, CompositeDisposable compositeDisposable, BackendNwHelper backendNwHelper, IFormManagementContract iFormManagementContract) {
        super(mvpInteractor, compositeDisposable, backendNwHelper, iFormManagementContract);
    }

    @Override
    public void onFillFormsOptionClicked() {
        getIFormManagementContract().launchFormChooserView(getMvpView().getActivityContext(), UtilityFunctions.generateToolbarModificationObject(true,
                R.drawable.ic_arrow_back_white_24dp, getMvpView().getActivityContext().getResources().getString(R.string.please_select_forms), true));
    }

    @Override
    public void fetchWelcomeText() {
        Grove.d("Fetching welcome text for the user...");
        if (getMvpView() != null)
            getMvpView().displayHomeWelcomeText(getMvpInteractor().getUserFullName());
    }

    @Override
    public void fetchStudentData(Context activityContext) {
        //TODO Add forced download check
        long ff = System.currentTimeMillis();
        if (!getMvpInteractor().getPreferenceHelper().hasDownloadedStudentData()) {
            studentDownloadStatus = FormDownloadStatus.DOWNLOADING;
            Grove.d("Starting time at fetching School Data is " + ff);
            int schoolCode = getMvpInteractor().getPreferenceHelper().fetchSchoolCode();
            String code = String.valueOf(schoolCode);
            if (schoolCode != 0 && (isTeacherAccount() || isSchoolAccount() || isUserSchoolHead())) {
                Grove.d("School code is " + code);
                IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
                iStudentDetailsContract.fetchStudentData(code, new ApolloQueryResponseListener<GetStudentsForSchoolQuery.Data>() {
                    @Override
                    public void onResponseReceived(Response<GetStudentsForSchoolQuery.Data> response) {
                        studentDownloadStatus = FormDownloadStatus.SUCCESS;
                        getMvpInteractor().getPreferenceHelper().downloadedStudentData(true);
                        long gg = System.currentTimeMillis();
                        if (formsDownloadStatus == FormDownloadStatus.SUCCESS)
                            ((HomeActivity) activityContext).renderLayoutVisible();

                        if (response != null) {
                            Grove.d("Size of student data is " + response.getData().student().size());
                        }
                        long timeTaken = gg - ff;
                        Timber.d("ending time at fetching School Data is  " + gg + "   time for n/w call  " + timeTaken);
                    }

                    @Override
                    public void onFailureReceived(ApolloException e) {
                        studentDownloadStatus = FormDownloadStatus.SUCCESS;
                        Grove.d("Failed to download student data " + e.getLocalizedMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onViewSubmittedFormsOptionsClicked(Context activityContext) {
        Grove.d("User selects the option View Submitted Forms...");
            if (isSchoolAccount() || isTeacherAccount() || isUserSchoolHead()) {
                getIFormManagementContract().launchViewSubmittedFormsView(activityContext, UtilityFunctions.generateToolbarModificationObject(true,
                        R.drawable.ic_cross,
                        activityContext.getResources().getString(R.string.my_visits), true));
            } else {
                Intent intent = new Intent(activityContext, SubmissionsActivity.class);
               activityContext.startActivity(intent);
            }
    }

    @Override
    public void onSubmitFormsClicked() {
        Grove.d("User selects the option Submit Forms...");
        if (getMvpView() != null) {
            getIFormManagementContract().launchViewUnsubmittedFormView(getMvpView().getActivityContext(), getClass().getName(), UtilityFunctions.generateToolbarModificationObject(true,
                    R.drawable.ic_cross,
                    getMvpView().getActivityContext().getResources().getString(R.string.submit_saved_forms), true));
        }
    }

    @Override
    public void onViewHelplineClicked() {
        Grove.d("User selects the option View Helpline...");
        if (getMvpView() != null) {
            Intent i = new Intent(getMvpView().getActivityContext(), ComingSoon.class);
            i.putExtra("helpline", false);
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
     *
     * @param version         New Form Version
     * @param previousVersion Old Form Version from preferences
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

    private boolean isTeacherAccount() {
        return getMvpInteractor().getPreferenceHelper().isTeacher();
        //Viewing and download of forms is based on User's role, you can configure it via Preferences when logging in as per User's Login response
    }

    private boolean isSchoolAccount() {
        return getMvpInteractor().getPreferenceHelper().isSchool();
        //Viewing and download of forms is based on User's role, you can configure it via Preferences when logging in as per User's Login response
    }


    private boolean isUserSchoolHead() {
        return getMvpInteractor().getPreferenceHelper().isUserSchoolHead();
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

        String roleMapping = MyApplication.getmFirebaseRemoteConfig().getString("role_mapping_1");
        Grove.d("Finding the remote Role Mapping for the user:");
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
    public void checkForFormUpdates(boolean isStoragePermissionAvailable, Context context) {
        Grove.d("Checking for form updates");
        if (isSchoolAccount() || isUserSchoolHead() || isTeacherAccount()) {
            if(!getMvpInteractor().getPreferenceHelper().eInstallSendCOunt())
                sendInstallInfo();
            getMvpInteractor().getPreferenceHelper().updateInstallSendCOunt(true);
        }
        ((HomeActivity) context).renderLayoutInvisible();
        String latestFormVrsion = MyApplication.getmFirebaseRemoteConfig().getString("version");
        String previousVersion = getMvpInteractor().getPreferenceHelper().getFormVersion();
        Grove.d("First cal vgfvgv >>> " + getUserRoleFromPref() );
        String formsString = MyApplication.getmFirebaseRemoteConfig().getString(getRoleFromRoleMappingFirebase(getUserRoleFromPref()));
        Grove.d("second cal vgfvgv >>> " + getUserRoleFromPref() );

        formsDownloadStatus = FormDownloadStatus.DOWNLOADING;
        isPermissionGiven = isStoragePermissionAvailable;
        Grove.e("Checking if the forms are matching: %s", getIFormManagementContract().checkIfODKFormsMatch(formsString));
        if (isNetworkConnected()) {
            if (isUpversioned(latestFormVrsion, previousVersion) ||
                    !getIFormManagementContract().checkIfODKFormsMatch(formsString)) {
                Grove.d("Forms have been up-versioned/ odk forms don't match with one needed for the user...");
                getMvpInteractor().getPreferenceHelper().updateFormVersion(latestFormVrsion);
                // Downloading new forms list.
                Grove.d("Starting Form List Download Task");
                getIFormManagementContract().startDownloadODKFormListTask(new FormListDownloadListener(context));
                formsDownloadStatus = FormDownloadStatus.DOWNLOADING;
            } else {
                Grove.d("Network Available, forms are matching, rendering the layout");
                if (getMvpView() != null) {
                    Grove.d("Rendering UI Visible as forms already downloaded");
                    if (isSchoolAccount() || isUserSchoolHead()) {
                        buildCSV();
                        buildCSVForTeachers(context);
                    }
                    renderLayoutVisible(context);

                } else {
                    if (isSchoolAccount() || isUserSchoolHead()) {
                        buildCSV();
                        buildCSVForTeachers(context);
                    }
                }
                formsDownloadStatus = FormDownloadStatus.SUCCESS;
            }
        } else {
            Grove.d("Network not available, can't download forms/ check versions, rendering layou");
            if (getMvpView() != null) {
                Grove.d("Rendering UI Visible as forms already downloaded");
                renderLayoutVisible(context);
                ((HomeActivity) context).showNoInternetMessage();
            }
        }
    }

    @Override
    public ArrayList<UserProfileElement> getProfileConfig() {
        Grove.d("Fetching the User Profile Params");
        String configString = MyApplication.getmFirebaseRemoteConfig().getString("profileConfig");
        if (configString.equals("")) configString = KeyboardHandler.config;
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
//        getIFormManagementContract().resetEverythingODK();
    }

    @Override
    public void resetProgressVariables() {
        currentProgress = 0;
    }

    @Override
    public void prefillData(InstitutionInfo institutionInfo) {
        Grove.d("Pre-filling data for the Forms downloaded");
        List<Form> forms = getIFormManagementContract().getDownloadedFormsNamesFromDatabase();
        for (Form form : forms) {
            String formName = form.getDisplayName();
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "district", institutionInfo.getDistrict());
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "block", institutionInfo.getBlock());
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "school", institutionInfo.getSchoolName());
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "user_name", getMvpInteractor().getUserName());
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "name", getMvpInteractor().getUserFullName());
            getIFormManagementContract().updateFormBasedOnIdentifier(formName, "designation", getUserRoleFromPref());
        }
    }

    @Override
    public void searchmodule() {
        if (getMvpView() != null) {
            if (isSchoolAccount() || isTeacherAccount() || isUserSchoolHead()) {
                getMvpInteractor().getPreferenceHelper().prefillSchoolInfo();
            }
            getMvpView().launchSearchModule();
        }
    }

    public void checkForDownloadStudentData(Context activityContext) {
        if ((getMvpInteractor().getPreferenceHelper().isSchoolUpdated() ||
                !getMvpInteractor().getPreferenceHelper().hasDownloadedStudentData()) && studentDownloadStatus != FormDownloadStatus.DOWNLOADING) {
            getMvpInteractor().getPreferenceHelper().downloadedStudentData(false);
            fetchStudentData(activityContext);
        }
    }

    public void fetchSchoolEmployeeData(Context activityContext) {
        if (isSchoolAccount() || isUserSchoolHead()) {
            IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
            iStudentDetailsContract.fetchSchoolInfo(String.valueOf(getMvpInteractor().getPreferenceHelper().fetchSchoolCode())
                    , getMvpInteractor().getPreferenceHelper().fetchSchoolName(), new EmployeeInfoListener() {
                        @Override
                        public void onSuccess(List<SchoolEmployeesInfo> employeeInfos) {
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
        }
    }

    public boolean hasSeenDialog() {
        return getMvpInteractor().getPreferenceHelper().hasSeenDialog();
    }

    public void updateSeenDialogCount() {
        getMvpInteractor().getPreferenceHelper().updateCountFlag(true);
    }

    public void setStudentData() {
        getMvpInteractor().getPreferenceHelper().downloadedStudentData(false);
    }

    class FormListDownloadListener implements FormListDownloadResultCallback {
        Context context;

        public FormListDownloadListener(Context context) {
            this.context = context;
        }

        @Override
        public void onSuccessfulFormListDownload(HashMap<String, ServerFormDetails> latestFormListFromServer) {
            Grove.d("FormList download complete "+ latestFormListFromServer.size()+" is the form list size");
            String formsString = MyApplication.getmFirebaseRemoteConfig().getString(getRoleFromRoleMappingFirebase(getUserRoleFromPref()));
            Grove.d("thirssss cal vgfvgv >>> " + getUserRoleFromPref() );
            HashMap<String, String> userRoleBasedForms = getIFormManagementContract().downloadFormList(formsString);
            // Download Forms if updates available or if forms not downloaded. Delete forms if not applied for the role.
            HashMap<String, ServerFormDetails> formsToBeDownloaded = getIFormManagementContract().downloadNewFormsBasedOnDownloadedFormList(userRoleBasedForms, latestFormListFromServer);
            if (formsToBeDownloaded.size() > 0) {
               Grove.d("Number of forms to be downloaded are "+ formsToBeDownloaded );
                formsDownloadStatus = FormDownloadStatus.DOWNLOADING;
                currentProgress = 2;
                currentProgress = 30;
            } else {
               Grove.e("No new forms to be downloaded for the user inspite of requirement, username is" + getMvpInteractor().getUserName() );
                if (isSchoolAccount() || isUserSchoolHead()) {
                    buildCSV();
                    buildCSVForTeachers(context);
                }
                formsDownloadStatus = FormDownloadStatus.SUCCESS;
                renderLayoutVisible(context);
            }
            if (formsDownloadStatus == FormDownloadStatus.DOWNLOADING)
                getIFormManagementContract().downloadODKForms(new FormDownloadListener(context), formsToBeDownloaded, true);
        }

        @Override
        public void onFailureFormListDownload(boolean isAPIFailure, String message) {

            Grove.e("There has been an error in downloading the forms from ODK Server for user" + getMvpInteractor().getUserName() + " failure is" +  message);
            if (isAPIFailure) {
                ((HomeActivity) context).showDownloadFailureMessage();
                formsDownloadStatus = FormDownloadStatus.FAILURE;
            }
            ((HomeActivity) context).renderLayoutVisible();

            if (!isNetworkConnected()) {
                ((HomeActivity) context).showNoInternetMessage();
            } else {
                ((HomeActivity) context).showFailureDownloadMessage();
            }
        }

        class FormDownloadListener implements DataFormDownloadResultCallback {
            Context context;

            public FormDownloadListener(Context context) {
                this.context = context;
            }

            @Override
            public void formsDownloadingSuccessful(Map<ServerFormDetails, String> result) {
                Grove.d("Form Download Complete %s", result);
                formsDownloadStatus = FormDownloadStatus.SUCCESS;
                if (isSchoolAccount() || isUserSchoolHead()) {
                    buildCSV();
                    buildCSVForTeachers(context);
                }
                    if (!isSchoolAccount() && !isUserSchoolHead()) {
                    ((HomeActivity) context).renderLayoutVisible();
                }
            }

            @Override
            public void formsDownloadingFailure() {
                Grove.d("Unable to download the forms");
                ((HomeActivity) context).showSnackbar("Could not download the forms", Snackbar.LENGTH_LONG);
                ((HomeActivity) context).renderLayoutVisible();
            }

            @Override
            public void progressUpdate(String currentFile, int progress, int total) {
                Grove.v("Form Download InProgress = " + currentFile + " Progress" + progress + " Out of=" + total);
                Grove.d(" Total%s", String.valueOf(total));
                Grove.d(" Total Progress %s", String.valueOf(progress));
                int formProgress = (progress * 100) / total;
                Grove.d("Form Download Progress: %s", formProgress);
                currentProgress = currentProgress + 70 / 9;
                if (formProgress == 100) {
                    currentProgress = 100;
                    Grove.d("Rendering UI Visible as forms already downloaded not, but now downloaded");
                    formsDownloadStatus = FormDownloadStatus.SUCCESS;
                }
            }

            @Override
            public void formsDownloadingCancelled() {
                ((HomeActivity) context).renderLayoutVisible();
                ((HomeActivity) context).showFailureDownloadMessage();
                Grove.e("Form Download Cancelled >> API Cancelled callback received");
            }
        }
    }

    private void sendInstallInfo() {
        IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
        iStudentDetailsContract.updateUsageInfo(getMvpInteractor().getUserName(), getMvpInteractor().getPreferenceHelper().getBlock(),
                getMvpInteractor().getPreferenceHelper().fetchSchoolName(),
                getMvpInteractor().getPreferenceHelper().fetchSchoolCode1(), getUserRoleFromPref(),
                getMvpInteractor().getPreferenceHelper().getDistrict(), getMvpInteractor().getUserName(),
                new ApolloQueryResponseListener<SendUsageInfoMutation.Data>() {
                    @Override
                    public void onResponseReceived(Response<SendUsageInfoMutation.Data> response) {
                        Grove.d("Successfully send Install Info");
                    }
                    @Override
                    public void onFailureReceived(ApolloException e) {
                        Grove.e("Unable to send the install info for the user");
                    }
                });
    }

    private void renderLayoutVisible(Context context) {
        ((HomeActivity) context).renderLayoutVisible();
    }

    private void buildCSVForTeachers(Context context) {
        String referenceFileName = "itemsets.csv";
        IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
        ArrayList<ArrayList<String>> list = iStudentDetailsContract.buildJSONArrayForEmployees();
        ArrayList<String> mediaDirectoriesNames = isPermissionGiven ? CSVHelper.fetchFormMediaDirectoriesWithMedia(referenceFileName) : new ArrayList<>();
        if (mediaDirectoriesNames.size() > 0 && list.size() > 0) {
            String direcName = "";
            for (String name : mediaDirectoriesNames) {
                String formName = name.toLowerCase();
                String teacher = "teacher";
                String COVID = "covid";
                if (formName.contains(teacher) && formName.contains(COVID)) {
                    direcName = name;
                    break;
                }
            }
            ArrayList<String> teacherCOVIDReportingMediaFile = new ArrayList<>();
            teacherCOVIDReportingMediaFile.add(direcName);
            CSVHelper.buildCSVForODK1(new CSVBuildStatusListener() {
                @Override
                public void onSuccess() {
                    ((HomeActivity) context).renderLayoutVisible();
                }

                @Override
                public void onFailure(Exception exception, CSVHelper.BuildFailureType buildFailureType) {
                    Grove.e(exception);
                }
            }, teacherCOVIDReportingMediaFile, null, referenceFileName, list);

        }
    }

    @Override
    public ArrayList<String> fetchHomeItemList() {
        ArrayList<String> atHomeItemsList = new ArrayList<>();
        if (isSchoolAccount()) {
            atHomeItemsList.add("Mark Teacher Attendance");
            atHomeItemsList.add("View Teacher Attendance");
            atHomeItemsList.add("Fill Forms");
            atHomeItemsList.add("View Forms");
            atHomeItemsList.add("Helpline");
            atHomeItemsList.add("Submit Forms");
            return atHomeItemsList;
        } else if (isTeacherAccount()) {
            atHomeItemsList.add("Mark Student Attendance");
            atHomeItemsList.add("View Student Attendance");
            atHomeItemsList.add("Edit Student Data");
            atHomeItemsList.add("Helpline");
            atHomeItemsList.add("Fill Forms");
            atHomeItemsList.add("View Forms");
            atHomeItemsList.add("Submit Forms");
            return atHomeItemsList;
        } else if (isUserSchoolHead()) {
            atHomeItemsList.add("Mark Teacher Attendance");
            atHomeItemsList.add("Mark Student Attendance");
            atHomeItemsList.add("View Teacher Attendance");
            atHomeItemsList.add("View Student Attendance");
            atHomeItemsList.add("Edit Student Data");
            atHomeItemsList.add("Fill Forms");
            atHomeItemsList.add("View Forms");
            atHomeItemsList.add("Helpline");
            atHomeItemsList.add("Submit Forms");
            return atHomeItemsList;
        }
        String role = getRoleFromRoleMappingFirebase(getUserRoleFromPref());
        Grove.d("four cal vgfvgv >>> " + getUserRoleFromPref() );

        switch (role) {
            case "Teacher":
                atHomeItemsList.add("Fill Forms");
                atHomeItemsList.add("Edit Student Data");
                atHomeItemsList.add("Mark Student Attendance");
                atHomeItemsList.add("View Student Attendance");
                atHomeItemsList.add("View Forms");
                atHomeItemsList.add("Submit Forms");
                atHomeItemsList.add("Helpline");
                break;
            case "Principal":
                atHomeItemsList.add("Fill Forms");
                atHomeItemsList.add("Mark Teacher Attendance");
                atHomeItemsList.add("View Teacher Attendance");
                atHomeItemsList.add("View School Attendance");
                atHomeItemsList.add("View Forms");
                atHomeItemsList.add("Submit Forms");
                atHomeItemsList.add("Helpline");
                break;
            case "School":
                atHomeItemsList.add("Fill Forms");
                atHomeItemsList.add("Mark Teacher Attendance");
                atHomeItemsList.add("View Teacher Attendance");
                atHomeItemsList.add("View Forms");
                atHomeItemsList.add("Submit Forms");
                atHomeItemsList.add("Helpline");
                break;
            default:
                atHomeItemsList.add("Fill Forms");
                atHomeItemsList.add("View Forms");
                atHomeItemsList.add("Submit Forms");
                atHomeItemsList.add("Helpline");
                break;
        }
        return atHomeItemsList;
    }

    @Override
    public boolean isProfileComplete() {
        return getMvpInteractor().getPreferenceHelper().isProfileComplete();
    }

    private void buildCSV() {
        String referenceFileName = "itemsets.csv"; String student = "student"; String COVID = "covid";
        IStudentDetailsContract iStudentDetailsContract = StudentDetailsComponentManager.iStudentDetailsContract;
        ArrayList<ArrayList<String>> list = iStudentDetailsContract.buildJSONArray();
        ArrayList<String> mediaDirectoriesNames = isPermissionGiven ? CSVHelper.fetchFormMediaDirectoriesWithMedia(referenceFileName) : new ArrayList<>();
        if (mediaDirectoriesNames.size() > 0 && list.size() > 0) {
            String direcName = "";
            for (String name : mediaDirectoriesNames) {
                String formName = name.toLowerCase();
                if (formName.contains(student) && formName.contains(COVID)) {
                    direcName = name;
                    break;
                }
            }
            ArrayList<String> studentReportingMediaFileList = new ArrayList<>();
            studentReportingMediaFileList.add(direcName);
            CSVHelper.buildCSVForODK1(new CSVBuildStatusListener() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onFailure(Exception exception, CSVHelper.BuildFailureType buildFailureType) {
                    Grove.e(exception);
                }
            }, studentReportingMediaFileList, list, referenceFileName, null);
        }
    }
}