package org.odk.collect.android.contracts;

import android.content.Context;

import com.samagra.commons.MainApplication;

import org.odk.collect.android.dto.Form;
import org.odk.collect.android.listeners.DownloadFormsTaskListener;
import org.odk.collect.android.listeners.FormListDownloaderListener;
import org.odk.collect.android.logic.FormDetails;

import java.util.HashMap;
import java.util.List;


public interface IFormManagementContract {

    void setODKModuleStyle(MainApplication mainApplication, int splashScreenDrawableID, int baseAppThemeStyleID,
                           int formActivityThemeID, int customThemeId_Settings, long toolbarIconResId);

    void resetPreviousODKForms();

    void resetEverythingODK();

    void createODKDirectories();

    void resetSelectedODKForms(Context context);

    void resetODKForms(Context context);

    boolean checkIfODKFormsMatch(String formsString);

    List<Form> getDownloadedFormsNamesFromDatabase();

    void startDownloadODKFormListTask(FormListDownloadResultCallback formListDownloadResultCallback);

    HashMap<String, String> downloadNewFormsBasedOnDownloadedFormList(HashMap<String, String> userRoleBasedForms, HashMap<String, FormDetails> latestFormListFromServer);

    void downloadODKForms(DataFormDownloadResultCallback dataFormDownloadResultCallback, HashMap<String, String> formsToBeDownloaded);

    HashMap<String, String> downloadFormList(String formsString);

    void initialiseODKProps();

    void applyODKCollectSettings(Context context, int inputStream);
}