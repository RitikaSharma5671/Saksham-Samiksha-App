package com.samagra.ancillaryscreens.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.di.component.ActivityComponent;
import com.samagra.ancillaryscreens.di.component.DaggerActivityComponent;
import com.samagra.ancillaryscreens.di.modules.CommonsActivityModule;
import com.samagra.commons.Constants;
import com.samagra.commons.LocaleManager;

import org.odk.collect.android.utilities.LocaleHelper;

import java.util.Locale;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * This abstract class serves as the Base for all other activities used in this module. The class is
 * designed to support MVP Pattern with Dagger support. Any methods that need to be executed in all
 * activities, must be mentioned here. App level configuration changes (like theme change, language change, etc)
 * can be easily made through a BaseActivity. This must implement {@link MvpView}.
 *
 * @author Pranav Sharma
 */
public abstract class BaseActivity extends AppCompatActivity implements MvpView {

    private ActivityComponent activityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetTitles();
    }

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .commonsActivityModule(new CommonsActivityModule(this))
                    .build();
        }
        return activityComponent;
    }


    @Override
    public String fetchString(int stringID) {
       return getActivityContext().getResources().getString(stringID);
    }

    protected void resetTitles() {
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
            if (info.labelRes != 0) {
                setTitle(info.labelRes);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void showSnackbar(String message, int duration) {
        if(message.equals("Multiple Users")) {
            Snackbar.make(findViewById(android.R.id.content),
                    getActivityContext().getResources().getString(R.string.mulitple_users_same_email_found), duration).show();
        }else if(message.equals("Success")) {
            Snackbar.make(findViewById(android.R.id.content),
                    getActivityContext().getResources().getString(R.string.successful_update), duration).show();
        }else if(message.equals("Failed to update user profile.")) {
            Snackbar.make(findViewById(android.R.id.content),
                    getActivityContext().getResources().getString(R.string.update_failed), duration).show();
        }else {
            Snackbar.make(findViewById(android.R.id.content), message, duration).show();
        }
    }
}
