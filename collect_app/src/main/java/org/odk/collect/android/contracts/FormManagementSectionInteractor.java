package org.odk.collect.android.contracts;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.samagra.commons.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.FillBlankFormActivity;
import org.odk.collect.android.activities.InstanceChooserList;
import org.odk.collect.android.activities.InstanceUploaderListActivity;
import org.odk.collect.android.activities.StorageMigrationActivity;
import org.odk.collect.android.application.Collect1;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.dao.helpers.ContentResolverHelper;
import org.odk.collect.android.formmanagement.ServerFormDetails;
import org.odk.collect.android.forms.Form;
import org.odk.collect.android.forms.FormSourceException;
import org.odk.collect.android.listeners.DownloadFormsTaskListener;
import org.odk.collect.android.listeners.FormListDownloaderListener;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.storage.StorageInitializer;
import org.odk.collect.android.storage.StorageSubdirectory;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.utilities.ApplicationConstants;
import org.odk.collect.android.utilities.ApplicationResetter;
import org.odk.collect.android.utilities.MultiClickGuard;
import org.odk.collect.android.utilities.ThemeUtils;
import org.odk.collect.android.utilities.WebCredentialsUtils;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import timber.log.Timber;

@SuppressWarnings({"rawtypes", "unchecked"})
public class FormManagementSectionInteractor
        implements IFormManagementContract {
    @Override
    public void resetPreviousODKForms(IResetActionListener iResetActionListener) {
        final List<Integer> resetActions = new ArrayList<>();
        resetActions.add(ApplicationResetter.ResetAction.RESET_FORMS);
        resetActions.add(ApplicationResetter.ResetAction.RESET_INSTANCES);
        resetActions.add(ApplicationResetter.ResetAction.RESET_LAYERS);
        resetActions.add(ApplicationResetter.ResetAction.RESET_OSM_DROID);
//        Runnable runnable = () -> {
//            new ApplicationResetter().reset(Collect1.getInstance().getAppContext(), resetActions);
//        };
//        new Thread(runnable).start();
//        new InstancesDao().deleteInstancesDatabase();
        new AsyncTask<Void, Void, List<Integer>>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected List<Integer> doInBackground(Void... voids) {
                return new ApplicationResetter().reset( resetActions);
            }

            @Override
            protected void onPostExecute(List<Integer> failedResetActions) {
                iResetActionListener.onResetActionDone(failedResetActions);

            }
        }.execute();
    }

    @Override
    public List<Form> getDownloadedFormsNamesFromDatabase() {
        FormsDao fd = new FormsDao();
        Cursor cursor = fd.getFormsCursor();
        return FormsDao.getFormsFromCursor(cursor);
    }

    @Override
    public void resetEverythingODK(Context context, IResetActionListener iResetActionListener) {
        final List<Integer> resetActions = new ArrayList<>();
        resetActions.add(ApplicationResetter.ResetAction.RESET_FORMS);
        resetActions.add(ApplicationResetter.ResetAction.RESET_INSTANCES);
        resetActions.add(ApplicationResetter.ResetAction.RESET_PREFERENCES);
        resetActions.add(ApplicationResetter.ResetAction.RESET_LAYERS);
        resetActions.add(ApplicationResetter.ResetAction.RESET_CACHE);
        resetActions.add(ApplicationResetter.ResetAction.RESET_OSM_DROID);
        new AsyncTask<Void, Void, List<Integer>>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected List<Integer> doInBackground(Void... voids) {
                return new ApplicationResetter().reset(resetActions);
            }

            @Override
            protected void onPostExecute(List<Integer> failedResetActions) {
                iResetActionListener.onResetActionDone(failedResetActions);

            }
        }.execute();
    }

    @Override
    public void createODKDirectories() {
        new StorageInitializer().createOdkDirsOnStorage();
    }

    @Override
    public void resetODKForms(Context context, IResetActionListener iResetActionListener) {
        final List<Integer> resetActions = new ArrayList<>();
        resetActions.add(ApplicationResetter.ResetAction.RESET_FORMS);
        resetActions.add(ApplicationResetter.ResetAction.RESET_INSTANCES);
        resetActions.add(ApplicationResetter.ResetAction.RESET_LAYERS);
        resetActions.add(ApplicationResetter.ResetAction.RESET_CACHE);
        resetActions.add(ApplicationResetter.ResetAction.RESET_OSM_DROID);
        new InstancesDao().deleteInstancesDatabase();
        new AsyncTask<Void, Void, List<Integer>>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected List<Integer> doInBackground(Void... voids) {
                return new ApplicationResetter().reset(resetActions);
            }

            @Override
            protected void onPostExecute(List<Integer> failedResetActions) {
                iResetActionListener.onResetActionDone(failedResetActions);
            }
        }.execute();
    }

    @Override
    public boolean checkIfODKFormsMatch(String formsString) {
        HashMap<String, String> formsListToBeDownloaded = downloadFormList(formsString);
        Timber.d("formsListToBeDownloaded from Firebase has size : " + formsListToBeDownloaded.size() + " FormsFromDatabase existing has size: " + getDownloadedFormsNamesFromDatabase().size());
        return getDownloadedFormsNamesFromDatabase().size() == formsListToBeDownloaded.size() && getDownloadedFormsNamesFromDatabase().size() != 0;
    }

    @Override
    public void startDownloadODKFormListTask(FormListDownloadResultCallback formListDownloadResultCallback) {
        DownloadFormListTask downloadFormListTask = new DownloadFormListTask(Collect1.getInstance().getServerDetailsFetcher());
        downloadFormListTask.setDownloaderListener(new FormListDownloaderListener() {
            @Override
            public void formListDownloadingComplete(HashMap<String, ServerFormDetails> formList, FormSourceException exception) {
                if (exception == null) {
                    formListDownloadResultCallback.onSuccessfulFormListDownload(formList);
                } else {
//                    switch (exception.getType()) {
//                        case FETCH_ERROR:
//                        case UNREACHABLE:
//                            String dialogMessage = new FormSourceExceptionMapper(Collect1.getInstance().getAppContext()).getMessage(exception);
//                            String dialogTitle = Collect1.getInstance().getAppContext().getString(R.string.load_remote_form_error);
//
//                           break;
//
//                        case AUTH_REQUIRED:
//                            createAuthDialog();
//                            break;
//                    }
//                    if (value.containsKey("dlerrormessage")) {
                    formListDownloadResultCallback.onFailureFormListDownload(true);
//                    } else {
//                        formListDownloadResultCallback.onFailureFormListDownload(false);
//                    }
                }
            }
        });
        downloadFormListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public HashMap<String, ServerFormDetails> downloadNewFormsBasedOnDownloadedFormList(HashMap<String, String> userRoleBasedForms, HashMap<String, ServerFormDetails> latestFormListFromServer) {
        HashMap<String, String> formsToBeDownloaded = new HashMap<>();
        HashMap<String, ServerFormDetails> formsToBeDownloadedABC = new HashMap<>();
        List<Form> formsFromDB = getDownloadedFormsNamesFromDatabase();
        Iterator it = latestFormListFromServer.entrySet().iterator();
        // Delete excess forms
        ArrayList<String> formsToBeDeleted = new ArrayList<>();
        for (Form form : formsFromDB) {
            if (!userRoleBasedForms.containsKey(form.getJrFormId())) {
                formsToBeDeleted.add(form.getJrFormId());
            }
        }
        // Adding new forms
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ServerFormDetails fd = (ServerFormDetails) pair.getValue();
            String formID = fd.getFormId();
            boolean foundFormInDB = false;
            if (userRoleBasedForms.containsKey(fd.getFormId())) {
                for (Form form : formsFromDB) {
                    // Check if forms needs to be updated
                    if (form.getJrFormId().equals(fd.getFormId())) {
                        foundFormInDB = true;
                        boolean nullTest = false;
                        if (form.getJrVersion() == null && fd.getFormVersion() == null)
                            nullTest = true;
                        if (form.getJrVersion() == null && fd.getFormVersion() != null) {
                            formsToBeDownloaded.put(fd.getFormId(), fd.getFormName());
                            formsToBeDownloadedABC.put(fd.getFormId(), fd);
                            formsToBeDeleted.add(form.getJrFormId());
                        } else if (!nullTest && !form.getJrVersion().equals(fd.getFormVersion())) {
                            formsToBeDownloaded.put(fd.getFormId(), fd.getFormName());
                            formsToBeDownloadedABC.put(fd.getFormId(), fd);
                            formsToBeDeleted.add(form.getJrFormId());
                        }
                    }
                }
                if (!foundFormInDB) {
                    formsToBeDownloaded.put(fd.getFormId(), fd.getFormName());
                    formsToBeDownloadedABC.put(fd.getFormId(), fd);
                }
            }
        }
        if (formsToBeDeleted.size() > 0 && formsToBeDeleted.toArray() != null) {
            new FormsDao().deleteFormsFromIDs(formsToBeDeleted.toArray(new String[0]));
        }
        return formsToBeDownloadedABC;
    }

    @Override
    public void downloadODKForms(DataFormDownloadResultCallback dataFormDownloadResultCallback, HashMap<String, ServerFormDetails> formsToBeDownloaded, boolean isODKAggregate) {
        ArrayList<ServerFormDetails> filesToDownload = new ArrayList<>();
        Iterator it = formsToBeDownloaded.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String formID = ((ServerFormDetails) pair.getValue()).getFormId();
//                String fileName = Collect1.FORMS_PATH + File.separator + formName + ".xml";
            String serverURL = new WebCredentialsUtils().getServerUrlFromPreferences();
            String partURL = "/www/formXml?formId=";
            String downloadUrl = serverURL + partURL + formID;
            String manifestUrl = serverURL + "/xformsManifest?formId=" + formID;
            ServerFormDetails fm = new ServerFormDetails(
                    ((ServerFormDetails) pair.getValue()).getFormName(),
                    downloadUrl,
//                    null,
                    manifestUrl,
                    formID,
                    ((ServerFormDetails) pair.getValue()).getFormVersion(),
                    ((ServerFormDetails) pair.getValue()).getHash(),
                    false,
                    false,
                    ((ServerFormDetails) pair.getValue()).getManifest());
            filesToDownload.add(fm);
            it.remove();
        }
        final DownloadFormsTask[] downloadFormsTask = {new DownloadFormsTask(Collect1.getInstance().getFormDownloader())};
        downloadFormsTask[0].setDownloaderListener(new DownloadFormsTaskListener() {
            @Override
            public void formsDownloadingComplete(Map<ServerFormDetails, String> result) {

                if (downloadFormsTask[0] != null) {
                    downloadFormsTask[0].setDownloaderListener(null);
                }

                if (result != null) {
                    int successCount = 0;
                    int totalExpected = result.size();
                    for (Map.Entry<ServerFormDetails, String> entry : result.entrySet()) {
                        if (entry.getValue() != null && entry.getValue().equals("Success"))
                            successCount += 1;
                    }
                    if (successCount == totalExpected) {
                        dataFormDownloadResultCallback.formsDownloadingSuccessful(result);
                    } else {
                        dataFormDownloadResultCallback.formsDownloadingFailure();
                    }
                } else {
                    dataFormDownloadResultCallback.formsDownloadingFailure();
                }
            }


            @Override
            public void progressUpdate(String currentFile, int progress, int total) {
                dataFormDownloadResultCallback.progressUpdate(currentFile, progress, total);
            }

            @Override
            public void formsDownloadingCancelled() {
                if (downloadFormsTask[0] != null) {
                    downloadFormsTask[0].setDownloaderListener(null);
                    downloadFormsTask[0] = null;
                }

                dataFormDownloadResultCallback.formsDownloadingCancelled();

            }
        });


        downloadFormsTask[0].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, filesToDownload);
    }

    @Override
    public HashMap<String, String> downloadFormList(String formsString) {
        HashMap<String, String> userRoleBasedForms = new HashMap<>();
        Timber.e("Role Mapping");
        if (!formsString.equals("")) {
            try {
                JSONArray jsonArray = new JSONArray(formsString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    //TODO: Check if form with the newest version is already there.
                    String formID = object.getString("FormID");
                    String formName = object.getString("FormName");
                    if (shouldUpdate()) {
                        userRoleBasedForms.put(formID, formName);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return userRoleBasedForms;
    }

    @Override
    public void initialiseODKProps() {
    }

    @Override
    public void applyODKCollectSettings(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String settings = writer.toString();
        if (settings != null) {
            if (Collect1.getInstance().getSettingsImporter().fromJSON(settings)) {
                Timber.d(Collect1.getInstance().getAppContext().getResources().getString(R.string.settings_successfully_loaded_file_notification));
            } else {
                Timber.d(Collect1.getInstance().getAppContext().getResources().getString(R.string.corrupt_settings_file_notification));
            }
        }
    }

    @Override
    public void launchSpecificDataForm(Context context, String formIdentifier) {
        int formToBeOpened = fetchSpecificFormID(formIdentifier);
        Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, formToBeOpened);
        Intent intent = new Intent(Intent.ACTION_EDIT, formUri);
        intent.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
        intent.putExtra("formTitle", formIdentifier);
        context.startActivity(intent);
    }

    @Override
    public int fetchSpecificFormID(String formIdentifier) {
        List<Form> formsFromDB = getDownloadedFormsNamesFromDatabase();
        HashMap<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < formsFromDB.size(); i++) {
            hashMap.put(Integer.valueOf(formsFromDB.get(i).getId().toString()), formsFromDB.get(i).getDisplayName());
        }
        for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {
            if (entry.getValue().contains(formIdentifier))
                return entry.getKey();
        }
        return 1;
    }

    @Override
    public void launchViewUnsubmittedFormView(Context context, String className, HashMap<String, Object> toolbarModificationObject) {
        if (MultiClickGuard.allowClick(className)) {
            Intent i = new Intent(context, InstanceUploaderListActivity.class);
            i.putExtra(Constants.KEY_CUSTOMIZE_TOOLBAR, toolbarModificationObject);
            context.startActivity(i);
        }
    }

    @Override
    public void launchViewSubmittedFormsView(Context context, HashMap<String, Object> toolbarModificationObject) {
        Intent i = new Intent(context, InstanceChooserList.class);
        i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE,
                ApplicationConstants.FormModes.VIEW_SENT);
        i.putExtra(Constants.KEY_CUSTOMIZE_TOOLBAR, toolbarModificationObject);
        context.startActivity(i);
        HashMap<String, Object> extras = toolbarModificationObject;
//                UtilityFunctions.generateT/d Forms", true);
//        ODKDriver.launchInstanceUploaderListActivity(context, extras);
    }

    @Override
    public void launchFormChooserView(Context context, HashMap<String, Object> toolbarModificationObject) {
        Intent i = new Intent(context, FillBlankFormActivity.class);
        i.putExtra(Constants.KEY_CUSTOMIZE_TOOLBAR, toolbarModificationObject);
        i.putIntegerArrayListExtra(Constants.CUSTOM_TOOLBAR_ARRAYLIST_HIDE_IDS, null);
        context.startActivity(i);
    }

    @Override
    public void updateFormBasedOnIdentifier(String formIdentifier, String tag, String tagValue) {
        int id = fetchSpecificFormID(formIdentifier);
        Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, id);
        String fileName = ContentResolverHelper.getFormPath(formUri);
        FileOutputStream fos = null;
        try {
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            factory.setNamespaceAware(true);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = null;
//            document = factory.newSAXParser().parse(new File(fileName), new MySAXHandler());
            try {
                document = builder.parse(new File(fileName));
                document.getDocumentElement().normalize();
            } catch (Exception e) {
                Timber.d(" Exception for form " + formIdentifier + " exception is " + e.getMessage());
            }
            if (document != null) {
                prefillFormBasedOnTags(document, tag, tagValue);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(document);
                fos = new FileOutputStream(new File(fileName));
                StreamResult result = new StreamResult(fos);
                transformer.transform(source, result);
            }
        } catch (ParserConfigurationException | IOException | TransformerException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public ArrayList<String> fetchMediaDirs(String referenceFileName) {
        return CSVHelper.fetchFormMediaDirectoriesWithMedia(referenceFileName);
    }

    @Override
    public void buildCSV(CSVBuildStatusListener csvBuildStatusListener, ArrayList<String> mediaDirectoriesNames, JSONArray inputData, String mediaFileName) {
        CSVHelper.buildCSVForODK(csvBuildStatusListener, mediaDirectoriesNames, inputData, mediaFileName);
    }

    @Override
    public void observeStorageMigration(Context context) {
        if (!Collect1.getInstance().getStorageStateProvider().isScopedStorageUsed()) {
            context.startActivity(new Intent(context, StorageMigrationActivity.class));
        }
    }

    @Override
    public boolean isScopedStorageUsed() {
        return Collect1.getInstance().getStorageStateProvider().isScopedStorageUsed();
    }

    @Override
    public boolean allowClick(String name) {
        return MultiClickGuard.allowClick(name);
    }

    @Override
    public int getBottomDialogTheme() {
        return new ThemeUtils(Collect1.getInstance().getAppContext()).getBottomDialogTheme();
    }

    @Override
    public void enableUsingScopedStorage() {
        Collect1.getInstance().getStorageStateProvider().enableUsingScopedStorage();
    }

    @Override
    public String getFormsPath() {
        return Collect1.getInstance().getStoragePathProvider().getDirPath(StorageSubdirectory.FORMS);
    }

    @Override
    public String getRootPath() {
        return Collect1.getInstance().getStoragePathProvider().getStorageRootDirPath();
    }

    @Override
    public void sendAnalyticsAdoptionEvent(String s, boolean b) {
        if (b) {
            Collect1.getInstance().getAnalytics().logEvent("app_installed_school_teacher_" + s, "install_info", s);
        } else {
            Collect1.getInstance().getAnalytics().logEvent("app_installed_mentor_monitor_" + s, "install_info", s);
        }
    }

    //
    private Document prefillFormBasedOnTags(Document document, String tag, String tagValue) {
        try {
            if (document.getElementsByTagName(tag).item(0).getChildNodes().getLength() > 0)
                document.getElementsByTagName(tag).item(0).getChildNodes().item(0).setNodeValue(tagValue);
            else
                document.getElementsByTagName(tag).item(0).appendChild(document.createTextNode(tagValue));
        } catch (Exception e) {
            Timber.e("Unable to autofill: %s %s", tag, tagValue);
            return document;
        }
        return document;
    }

    private boolean shouldUpdate() {
        return true;
    }

}