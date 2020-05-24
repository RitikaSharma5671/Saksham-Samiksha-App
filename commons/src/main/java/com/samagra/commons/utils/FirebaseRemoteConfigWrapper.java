package com.samagra.commons.utils;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.samagra.grove.logging.Grove;

import java.util.HashMap;

/**
 * Created by Umang Bhola on 20/5/20.
 * Samagra- Transforming Governance
 */
public class FirebaseRemoteConfigWrapper {

    private static FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static void init(Context context) {
        FirebaseApp.initializeApp(context);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings).addOnCompleteListener(task -> mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Grove.d("Remote config activate successful. Config params updated :: %s", task1.getResult());
            } else {
                Grove.e("Remote config activation failed.");
            }
        }));
    }

    public static FirebaseRemoteConfig getRemoteConfig(Context context) {
        if (mFirebaseRemoteConfig == null) {
            synchronized (FirebaseRemoteConfigWrapper.class) {
                if (mFirebaseRemoteConfig != null)
                    return mFirebaseRemoteConfig;
                init(context);
            }
        }
        return mFirebaseRemoteConfig;
    }


}
