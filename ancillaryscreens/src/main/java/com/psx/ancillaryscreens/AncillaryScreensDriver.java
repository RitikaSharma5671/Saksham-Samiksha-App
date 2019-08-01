package com.psx.ancillaryscreens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.psx.ancillaryscreens.data.network.BackendCallHelperImpl;
import com.psx.ancillaryscreens.screens.about.AboutActivity;
import com.psx.ancillaryscreens.screens.about.AboutBundle;
import com.psx.ancillaryscreens.screens.login.LoginActivity;
import com.psx.commons.CommonUtilities;
import com.psx.commons.CustomEvents;
import com.psx.commons.ExchangeObject;
import com.psx.commons.MainApplication;
import com.psx.commons.Modules;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AncillaryScreensDriver {
    public static MainApplication mainApplication = null;
    public static String BASE_API_URL;

    public static void init(MainApplication mainApplication, String BASE_URL) {
        AncillaryScreensDriver.mainApplication = mainApplication;
        AncillaryScreensDriver.BASE_API_URL = BASE_URL;
    }

    public static void launchLoginScreen(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        CommonUtilities.startActivityAsNewTask(intent, context);
    }

    public static void launchAboutActivity(Context context, AboutBundle aboutBundle) {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.putExtra("config", aboutBundle.aboutExchangeBundle);
        context.startActivity(intent);
    }

    public static void performLogout(Context context) {
        // TODO : Logout button => Logout from fusionAuth => Update user by removing registration token => Login splash_ss
        notifyLogoutInitiated();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString("FCM.token", "");
        if (CommonUtilities.isNetworkAvailable(context))
            makeRemoveTokenApiCall(token, context);
        else
            Toast.makeText(context, "No Internet Connection. Please connect to internet and try again later", Toast.LENGTH_LONG).show();
    }


    /**
     * The private functions not exposed to classes outside the module
     */

    private static void makeRemoveTokenApiCall(String token, Context context) {
        String apiKey = context.getResources().getString(R.string.fusionauth_api_key);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString("user.id", "");
        BackendCallHelperImpl.getInstance()
                .performGetUserDetailsApiCall(userId, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("OnSubscribe make Remove token api call");
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Timber.d("OnSuccess make Remove token api call %s", jsonObject);
                        JSONObject removedFCMTokenObject = removeFCMTokenFromObject(jsonObject);
                        Timber.d("Removed FCM Token, new Object is %s", removedFCMTokenObject);
                        putUpdatedUserDetailsObject(removedFCMTokenObject, context, userId, apiKey);
                    }

                    @Override
                    public void onError(Throwable e) {
                        notifyLogoutCompleted();
                        Timber.e(e);
                        Toast.makeText(context, "Unable to Log you out, Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static JSONObject removeFCMTokenFromObject(JSONObject jsonObject) {
        Timber.d("Removing FCM Token from %s", jsonObject.toString());
        try {
            JSONObject user = jsonObject.getJSONObject("user");
            JSONObject data = user.getJSONObject("data");
            data.put("FCM.token", "");
            user.put("data", data);
            jsonObject.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static void putUpdatedUserDetailsObject(JSONObject jsonObjectToPut, Context context, String userId, String apiKey) {
        BackendCallHelperImpl.getInstance()
                .performPutUserDetailsApiCall(userId, apiKey, jsonObjectToPut)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("On Subscribe Put updated objects ");
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Timber.d("Successfully removed FCM TOKEN, %s", jsonObject);
                        notifyLogoutCompleted();
                        logoutUserLocally(context);
                        Intent intent = new Intent(context, LoginActivity.class);
                        CommonUtilities.startActivityAsNewTask(intent, context);
                    }

                    @Override
                    public void onError(Throwable e) {
                        notifyLogoutCompleted();
                        Timber.e(e);
                        Toast.makeText(context, "Unable to Log you out, Some error occurred.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Update sharedPreferences to indicate Logout is successful
    private static void logoutUserLocally(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("updatedMappingThroughFirebase2");
        editor.remove("formVersion");
        editor.apply();
    }

    private static void notifyLogoutInitiated() {
        Timber.i("Logout initiated");
        ExchangeObject.EventExchangeObject eventExchangeObject = new ExchangeObject.EventExchangeObject(Modules.MAIN_APP, Modules.ANCILLARY_SCREENS, CustomEvents.LOGOUT_INITIATED);
        mainApplication.getEventBus().send(eventExchangeObject);
    }

    /**
     * Notifies about Logout process being completed, if required,
     * a success/failure status code may be sent along with the event
     */
    private static void notifyLogoutCompleted() {
        Timber.i("Logout completed");
        ExchangeObject.EventExchangeObject eventExchangeObject = new ExchangeObject.EventExchangeObject(Modules.MAIN_APP, Modules.ANCILLARY_SCREENS, CustomEvents.LOGOUT_COMPLETED);
        mainApplication.getEventBus().send(eventExchangeObject);
    }
}
