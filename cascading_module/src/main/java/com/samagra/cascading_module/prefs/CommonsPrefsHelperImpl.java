package com.samagra.cascading_module.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

import com.google.gson.Gson;
import com.samagra.cascading_module.di.ApplicationContext;
import com.samagra.cascading_module.di.PreferenceInfo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Solid implementation of {@link CommonsPreferenceHelper}, performs the read/write operations on the {@link SharedPreferences}
 * used by the ancillaryscreens. The class is injected to all activities instead of manually creating an object.
 *
 * @author Pranav Sharma
 */
public class CommonsPrefsHelperImpl implements CommonsPreferenceHelper {

    private SharedPreferences defaultPreferences;
    Context context;

    @Inject
    public CommonsPrefsHelperImpl(@ApplicationContext Context context, @PreferenceInfo String prefFileName){
        this.context = context;
        MasterKey mainKey = null;
        try {
            mainKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            defaultPreferences =
                    EncryptedSharedPreferences.create(
                            context, "SAMAGRA_PREFS",
                            mainKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
        } catch (GeneralSecurityException | IOException e) {
            defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    @Override
    public String getUserRoleFromPref() {
        try {
            return new HashMap<String, String>((Map) new Gson().fromJson(defaultPreferences.getString("user.data", ""), HashMap.class).get("roleData")).get("designation");
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getUserName() {
        return defaultPreferences.getString("user.fullName", "");
    }
}
