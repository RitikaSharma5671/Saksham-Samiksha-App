package com.psx.ancillaryscreens.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import timber.log.Timber;

/**
 * This class contains the common utilities that can be used independently in any module using this library.
 * All the functions in this class must be public static.
 */
public class CommonUtilities {

    /**
     * This functions takes the current {@link Context} and tells if the device is connected to the internet.
     *
     * @param context - Non-null {@link Context} of calling Activity.
     * @return boolean - true if internet available, false otherwise.
     */
    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } else {
            Timber.e("ConnectivityManager is null");
            return false;
        }
    }
}
