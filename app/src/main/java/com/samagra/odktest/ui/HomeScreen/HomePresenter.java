package com.samagra.odktest.ui.HomeScreen;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.reflect.TypeToken;
import com.google.firebase.FirebaseApp;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.samagra.ancillaryscreens.data.prefs.CommonsPrefsHelperImpl;
import com.samagra.ancillaryscreens.models.UserProfileElement;
import com.samagra.grove.Grove;
import com.samagra.odktest.MyApplication;
import com.samagra.odktest.R;
import com.samagra.odktest.base.BasePresenter;
import com.samagra.odktest.data.models.School;
import com.samagra.odktest.ui.SearchActivity.SearchActivity;
import com.samagra.odktest.ui.Submissions.SubmissionsActivity;
import com.samagra.odktest.ui.VisitsScreen.MyVisitsActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.ODKDriver;
import org.odk.collect.android.activities.WebViewActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dto.Form;
import org.odk.collect.android.listeners.ActionListener;
import org.odk.collect.android.listeners.DownloadFormsTaskListener;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.preferences.AdminSharedPreferences;
import org.odk.collect.android.preferences.GeneralSharedPreferences;
import org.odk.collect.android.preferences.PreferenceSaver;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.utilities.CustomTabHelper;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.android.utilities.WebCredentialsUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * The Presenter class for Home Screen. This class controls interaction between the View and Data.
 * This class <b>must</b> implement the {@link HomeMvpPresenter} and <b>must</b> be a type of {@link BasePresenter}.
 *
 * @author Pranav Sharma
 */
public class HomePresenter<V extends HomeMvpView, I extends HomeMvpInteractor> extends BasePresenter<V, I> implements HomeMvpPresenter<V, I> {

    // Used to maintain the state of Form download.
    enum Status {
        SUCCESS,
        DOWNLOADING,
        FAILURE;
    }
    private Status formsDownloadStatus = Status.FAILURE;
    private HashMap<String, String> formsToBeDownloaded = new HashMap<>();
    List<Form> formsFromDB;
    boolean updateStarted = false;

    /**
     * The injected values is provided through {@link com.samagra.odktest.di.modules.ActivityAbstractProviders}
     */
    @Inject
    public HomePresenter(I mvpInteractor) {
        super(mvpInteractor);
    }


    @Override
    public void onMyVisitClicked(View v) {
        launchActivity(SubmissionsActivity.class);
//        launchActivity(MyVisitsActivity.class);
    }

    @Override
    public void onInspectSchoolClicked(View v) {
        if (formsDownloadStatus.equals(Status.SUCCESS)) {
            startSearchActivity(SearchActivity.class);
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

    @NotNull
    private DownloadFormsTaskListener getDownloadFormTaskListener() {
        return new DownloadFormsTaskListener() {
            @Override
            public void formsDownloadingComplete(HashMap<FormDetails, String> result) {
                onFormsDownloadingComplete(result);
            }

            @Override
            public void progressUpdate(String currentFile, int progress, int total) {
                onProgressUpdate(currentFile, progress, total);
            }

            @Override
            public void formsDownloadingCancelled() {
                onFormsDownloadingCancelled();
            }
        };
    }

    @Override
    public void downloadForms(HashMap<String, String> formsToBeDownloaded) {
        ArrayList<FormDetails> filesToDownload = new ArrayList<>();
        Iterator it = formsToBeDownloaded.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String formName = pair.getValue().toString();
            String formID = pair.getKey().toString();
            String fileName = Collect.FORMS_PATH + File.separator + formName + ".xml";
            String serverURL = new WebCredentialsUtils().getServerUrlFromPreferences();
            String partURL = "/www/formXml?formId=";
            String downloadUrl = serverURL + partURL + formID;
            FormDetails fm = new FormDetails(
                    formName,
                    downloadUrl,
                    null,
                    formID,
                    "",
                    null,
                    null,
                    false,
                    false);
            filesToDownload.add(fm);
            it.remove();
        }
        DownloadFormsTask downloadFormsTask = new DownloadFormsTask();
        downloadFormsTask.setDownloaderListener(getDownloadFormTaskListener());
        downloadFormsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, filesToDownload);

        // Low internet connectivity
        // TODO: Add the progressbar with percentage progress.
        // formProgressBar.setVisibility(View.VISIBLE);
        // formProgressBar.setProgress(0);
    }

    @Override
    public void applySettings() {
        InputStream inputStream = getMvpView().getActivityContext().getResources().openRawResource(R.raw.settings);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String content = writer.toString();
        new PreferenceSaver(GeneralSharedPreferences.getInstance(), AdminSharedPreferences.getInstance()).fromJSON(content, new ActionListener() {
            @Override
            public void onSuccess() {
                Collect.getInstance().initProperties();
                ToastUtils.showLongToast("Successfully loaded settings");
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof GeneralSharedPreferences.ValidationException) {
                    ToastUtils.showLongToast("Failed to load settings");
                } else {
                    exception.printStackTrace();
                }
            }
        });
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

    public List<Form> getFormsFromDatabase() {
        FormsDao fd = new FormsDao();
        Cursor cursor = fd.getFormsCursor();
        return fd.getFormsFromCursor(cursor);
    }

    private boolean shouldUpdate(String formID, String formName) {
        List<Form> formsFromDB = getFormsFromDatabase();
        return true;
    }

    private void startSearchActivity(Class<?> cls) {
        Intent intent = new Intent(getMvpView().getActivityContext(), cls);
        Bundle bundle = new Bundle();
        List<Form> formsFromDB = getFormsFromDatabase();
        HashMap<String, String> formsToBeAutoFilled = new HashMap<>();
        for (Form form : formsFromDB) {
            formsToBeAutoFilled.put(form.getJrFormId(), form.getFormFilePath());
        }
        bundle.putSerializable("forms", formsToBeAutoFilled);
        intent.putExtras(bundle);
        launchActivity(cls, intent);
    }

    public HashMap<String, String> downloadFormList(String role) {
        if(!FirebaseApp.getApps(getMvpView().getActivityContext().getApplicationContext()).isEmpty()) {
            Timber.e("Firebase initialized");
        }
        HashMap<String, String> userRoleBasedForms = new HashMap<>();
        String formsString = MyApplication.getmFirebaseRemoteConfig().getString(role);
        Timber.e("Role Mapping");
        Timber.e(formsString);
        if (!formsString.equals("")) {
            try {
                JSONArray jsonArray = new JSONArray(formsString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    //TODO: Check if form with the newest version is already there.
                    String formID = object.getString("FormID");
                    String formName = object.getString("FormName");
                    if (shouldUpdate(formID, formName)) {
                        userRoleBasedForms.put(formID, formName);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return userRoleBasedForms;
    }

    public static void renameFile(String oldName, String newName) {
        File from = new File(oldName);
        File to = new File(newName);
        if (from.exists())
            from.renameTo(to);
    }

    private List<School> loadValuesToMemory() {
        List<School> schools = new ArrayList<>();
        File dataFile = new File(Collect.ODK_ROOT + "/data.json");
        try {
            JsonReader reader = new JsonReader(new FileReader(dataFile));

            Gson gson = new GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .setVersion(1.0)
                    .create();

            Type listType = new TypeToken<ArrayList<School>>() {
            }.getType();
            schools = gson.fromJson(reader, listType);

        } catch (Exception e) {
            Timber.e(e);
            Timber.e("Exception in loading data to memory");
        }
        return schools;
    }

    private String getUserRoleFromPref() {
        return getMvpInteractor().getPreferenceHelper().getUserRoleFromPref();
    }

    private boolean areFormsMatching() {
        HashMap<String, String> formsListToBeDownloaded = downloadFormList(getRoleFromRoleMappingFirebase(getUserRoleFromPref()));
        Timber.e("formsListToBeDownloaded: " + formsListToBeDownloaded.size() + " FormsFromDatabase: " + getFormsFromDatabase().size());
        boolean condition1 = getFormsFromDatabase().size() == formsListToBeDownloaded.size() && getFormsFromDatabase().size() != 0;
        return condition1;
    }

    String getRoleFromRoleMappingFirebase(String userRole) {
        if (userRole.equals("")) return "All";
        class RoleMapping {
            public String Directorate;
            public String Designation;
            public String Role;

            RoleMapping(String Directorate, String Designation, String Role) {
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
        updateStarted = true;
        String version;
        version = MyApplication.getmFirebaseRemoteConfig().getString("version");
        String previousVersion = getMvpInteractor().getPreferenceHelper().getFormVersion();
        String previousUdiseVersion = getMvpInteractor().getPreferenceHelper().getUdiseListVersion();
        String udiseListVersion = MyApplication.getmFirebaseRemoteConfig().getString("udiseListVersion");
        Timber.e("UDISElistversions" + " " + udiseListVersion + " " + previousUdiseVersion);
        Timber.e("Are forms matching: " +  areFormsMatching());
        if (isUpversioned(version, previousVersion) || !areFormsMatching()) {
            getMvpInteractor().getPreferenceHelper().updateFormVersion(version);

            // Downloading new forms list.
            DownloadFormListTask downloadFormListTask = new DownloadFormListTask(ODKDriver.getDownloadFormListUtils());
            downloadFormListTask.setDownloaderListener(value -> onFormListDownloadingComplete(value));
            downloadFormListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            formsDownloadStatus = Status.DOWNLOADING;
        } else {
            formsDownloadStatus = Status.SUCCESS;
        }
    }

    public HashMap<String, String> getFormsForUserFirebase() {
        String userRole = getUserRoleFromPref();
        return downloadFormList(getRoleFromRoleMappingFirebase(userRole));
    }

    private boolean isUpversioned(String version, String previousVersion) {
        try {
            return Integer.parseInt(version) > Integer.parseInt(previousVersion);
        } catch (Exception e) {
            return false;
        }
    }

    void onFormsDownloadingComplete(HashMap<FormDetails, String> result){
        Timber.e("Form Download Complete " + result);
        Iterator it = result.entrySet().iterator();
        formsDownloadStatus = Status.SUCCESS;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            FormDetails formDetails = (FormDetails) pair.getKey();
            String fileName = Collect.FORMS_PATH + File.separator + formDetails.getFormName() + ".xml";
            it.remove();
        }
    }

    public void onProgressUpdate(String currentFile, int progress, int total) {
        // formProgressBar.setVisibility(View.VISIBLE);
        Timber.v("Form Download InProgress = " + currentFile + " Progress" + String.valueOf(progress) + " Out of=" + String.valueOf(total));
        Log.d("Forms to download", String.valueOf(total));
        Log.d("Forms downloaded", String.valueOf(progress));
        int formProgress = (progress * 100) / total;
        Log.d("Progress", String.valueOf(formProgress));
        // formProgressBar.setProgress(formProgress);
        // if (formProgress == 100) formProgressBar.setVisibility(View.GONE);
        if (formProgress == 100) {
            formsDownloadStatus = Status.SUCCESS;
            // snackLinearLayout.setVisibility(View.GONE);
        }
    }

    public void onFormsDownloadingCancelled() {
        Timber.e("Form Download Cancelled");
    }

    public void onFormListDownloadingComplete(HashMap<String, FormDetails> latestFormListFromServer) {
        Timber.e("FormList download complete " + latestFormListFromServer);
        if (latestFormListFromServer.containsKey("dlerrormessage")) {
            Timber.e("There has been an error in downlaoding the forms from Aggregagte");
            checkForFormUpdates();
            return;
        }
        updateStarted = false;
        //formsDownloadStatus = Status.SUCCESS;

        HashMap<String, String> userRoleBasedForms = new HashMap<>();

        // Download Forms if updates available or if forms not downloaded. Delete forms if not applied for the role.
        userRoleBasedForms = getFormsForUserFirebase();


        formsFromDB = getFormsFromDatabase();
        Iterator it = latestFormListFromServer.entrySet().iterator();

        // Delete excess forms
        ArrayList<String> formsToBeDeleted = new ArrayList<>();
        for (Form form : formsFromDB) {
            if (!userRoleBasedForms.containsKey(form.getJrFormId())) {
                formsToBeDeleted.add(form.getMD5Hash());
            }
        }

        // Adding new forms
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            FormDetails fd = (FormDetails) pair.getValue();
            String formID = fd.getFormID();
            boolean foundFormInDB = false;
            if (userRoleBasedForms.containsKey(fd.getFormID())) {

                for (Form form : formsFromDB) {
                    // Check if forms needs to be updated
                    if (form.getJrFormId().equals(fd.getFormID())) {
                        foundFormInDB = true;
                        boolean nullTest = false;
                        if (form.getJrVersion() == null && fd.getFormVersion() == null)
                            nullTest = true;
                        if (form.getJrVersion() == null && fd.getFormVersion() != null) {
                            formsToBeDownloaded.put(fd.getFormID(), fd.getFormName());
                            formsToBeDeleted.add(form.getMD5Hash());
                        } else if (!nullTest && !form.getJrVersion().equals(fd.getFormVersion())) {
                            formsToBeDownloaded.put(fd.getFormID(), fd.getFormName());
                            formsToBeDeleted.add(form.getMD5Hash());
                        }
                    }
                }
                if (!foundFormInDB) formsToBeDownloaded.put(fd.getFormID(), fd.getFormName());
            }
        }
        if (formsToBeDeleted.size() > 0 && formsToBeDeleted.toArray() != null) {
            new FormsDao().deleteFormsFromMd5Hash(formsToBeDeleted.toArray(new String[0]));
        }
        if (formsToBeDownloaded.size() > 0) {
            downloadForms(formsToBeDownloaded);
        } else {
            formsDownloadStatus = Status.SUCCESS;
        }
    }

    public ArrayList<UserProfileElement> getProfileConfig() {
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
}
