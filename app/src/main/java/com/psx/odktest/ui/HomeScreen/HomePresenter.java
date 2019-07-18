package com.psx.odktest.ui.HomeScreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.psx.odktest.Constants;
import com.psx.odktest.R;
import com.psx.odktest.base.BasePresenter;

import org.odk.collect.android.activities.InstanceUploaderListActivity;
import org.odk.collect.android.activities.WebViewActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.ActionListener;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.preferences.AdminSharedPreferences;
import org.odk.collect.android.preferences.GeneralSharedPreferences;
import org.odk.collect.android.preferences.PreferenceSaver;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.utilities.CustomTabHelper;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.android.utilities.WebCredentialsUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

public class HomePresenter<V extends HomeMvpView, I extends HomeMvpInteractor> extends BasePresenter<V, I> implements HomeMvpPresenter<V, I> {

    @Inject
    public HomePresenter(I mvpInteractor) {
        super(mvpInteractor);
    }

    @Override
    public void onMyVisitClicked(View v) {
        // TODO : Create a MyVisits Activity and launch;
        Toast.makeText(getMvpView().getActivityContext(), "Not Implemented Yet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInspectSchoolClicked(View v) {
        // TODO : Create a Search Activity and launch;
        Toast.makeText(getMvpView().getActivityContext(), "Not Implemented Yet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubmitFormClicked(View v) {
        launchActivity(InstanceUploaderListActivity.class);
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
        // TODO : Do Some action ?
        Toast.makeText(getMvpView().getActivityContext(), "Not Implemented Yet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setWelcomeText() {
        getMvpView().updateWelcomeText(getMvpInteractor().getUserName());
    }

    @Override
    public void downloadForms() {
        for (Map.Entry<String, String> formEntry : Constants.FORM_LIST.entrySet()) {
            String fileName = Collect.FORMS_PATH + File.separator + formEntry.getValue() + ".xml";
            File file = new File(fileName);
            String serverURL = new WebCredentialsUtils().getServerUrlFromPreferences();
            String partURL = "/www/formXml?formId=";
            String downloadUrl = serverURL + partURL + formEntry.getKey();

            if (file.exists()) {
                Timber.i("File exists, won't download again");
            } else {
                ArrayList<FormDetails> filesToDownload = new ArrayList<>();
                FormDetails fm = new FormDetails(
                        formEntry.getValue(),
                        downloadUrl,
                        null,
                        formEntry.getKey(),
                        "",
                        null,
                        null,
                        false,
                        false);
                filesToDownload.add(fm);
                DownloadFormsTask downloadFormsTask = new DownloadFormsTask();
                downloadFormsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, filesToDownload);
            }
        }
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
}
