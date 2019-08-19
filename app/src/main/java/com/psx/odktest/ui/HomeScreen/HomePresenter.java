package com.psx.odktest.ui.HomeScreen;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import com.psx.odktest.AppConstants;
import com.psx.odktest.R;
import com.psx.odktest.base.BasePresenter;
import com.psx.odktest.ui.SearchActivity.SearchActivity;
import com.psx.odktest.ui.VisitsScreen.MyVisitsActivity;

import org.odk.collect.android.ODKDriver;
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

    /**
     * The injected values is provided through {@link com.psx.odktest.di.modules.ActivityAbstractProviders}
     */
    @Inject
    public HomePresenter(I mvpInteractor) {
        super(mvpInteractor);
    }

    @Override
    public void onMyVisitClicked(View v) {
        launchActivity(MyVisitsActivity.class);
    }

    @Override
    public void onInspectSchoolClicked(View v) {
        launchActivity(SearchActivity.class);
    }

    @Override
    public void onSubmitFormClicked(View v) {
        ODKDriver.launchInstanceUploaderListActivity(getMvpView().getActivityContext());
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
    public void downloadForms() {
        for (Map.Entry<String, String> formEntry : AppConstants.FORM_LIST.entrySet()) {
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
