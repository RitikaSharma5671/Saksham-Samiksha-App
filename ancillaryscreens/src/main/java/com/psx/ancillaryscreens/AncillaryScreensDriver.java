package com.psx.ancillaryscreens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

/**
 * The driver class for this module, any screen that needs to be launched from outside this module, should be
 * launched using this class.
 * Note: It is essential that you call the {@link AncillaryScreensDriver#init(MainApplication, String)} to initialise
 * the class prior to using it.
 *
 * @author Pranav Sharma
 */
public class AncillaryScreensDriver {
    public static MainApplication mainApplication = null;
    public static String BASE_API_URL;

    //TODO : Throw InvalidConfigurationException if this method is not called prior to using anything else.
    public static void init(MainApplication mainApplication, String BASE_URL) {
        AncillaryScreensDriver.mainApplication = mainApplication;
        AncillaryScreensDriver.BASE_API_URL = BASE_URL;
    }

    /**
     * Function to launch the login screen from this module. This function starts the {@link LoginActivity} as a new task,
     * clearing everything else in the activity back-stack.
     *
     * @param context - The current Activity's context.
     */
    public static void launchLoginScreen(@NonNull Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        CommonUtilities.startActivityAsNewTask(intent, context);
    }

    /**
     * Function to launch the about activity from this module.
     *
     * @param context     - The current Activity's context.
     * @param aboutBundle - A Java object containing values to configure {@link AboutActivity}'s appearance.
     * @see AboutBundle#AboutBundle(String, String, String, int, int, int)
     */
    public static void launchAboutActivity(@NonNull Context context, @NonNull AboutBundle aboutBundle) {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.putExtra("config", aboutBundle.aboutExchangeBundle);
        context.startActivity(intent);
    }

    /**
     * Function to perform the logout operations. This function uses the updates the flags and other data set by the
     * {@link LoginActivity} to reflect that the user has logged out. This function also notifies the logout progress using
     * RxJava EventBus system
     *
     * @param context - The current Activity's context.
     * @see AncillaryScreensDriver#notifyLogoutInitiated()
     * @see AncillaryScreensDriver#notifyLogoutCompleted()
     */
    public static void performLogout(@NonNull Context context) {
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
     * The private functions not exposed to classes outside the module. These functions help the public static functions
     * defined in this file to complete their functionality. These can also be moved to another file for sake of clarity.
     */

    /**
     * This function makes an API call to get the current User Object stored at the backend. This function is the first part
     * of a 2-step process to logout the user. The object retrieved by this API call is then edited to remove
     * the FCMToken from it. This is done to prevent logged out users from receiving notifications.
     *
     * @param token   - The API token for the fusionAuth API.
     * @param context - The current Activity Context.
     * @see AncillaryScreensDriver#removeFCMTokenFromObject(JSONObject) - This edits the JSONObject retrieved by removing the FCM token.
     * @see AncillaryScreensDriver#putUpdatedUserDetailsObject(JSONObject, Context, String, String)  - This uploads the new JSON Object as User data Object (with no FCM Token).
     */
    private static void makeRemoveTokenApiCall(@NonNull String token, @NonNull Context context) {
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

    /**
     * This function receives the User Object as {@link JSONObject} and removes the FCM Token from it.
     *
     * @param jsonObject - The user JSON Object from the backend APIs.
     * @return user object as {@link JSONObject} with empty FCM Token.
     */
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

    /**
     * This function makes an API call to post the updated User data as {@link JSONObject} returned by {@link AncillaryScreensDriver#removeFCMTokenFromObject(JSONObject)}.
     *
     * @param jsonObjectToPut - The user {@link JSONObject} without FCM Token.
     * @param context         - The current Activity Context.
     * @param userId          - String userId for the current User.
     * @param apiKey          - The FusionAuth ApiKey.
     */
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

    /**
     * This function updates the login related flags set by the {@link LoginActivity} to reflect that the user is now
     * logged out.
     *
     * @param context - The current Activity/Application context.
     */
    private static void logoutUserLocally(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("updatedMappingThroughFirebase2");
        editor.remove("formVersion");
        editor.apply();
    }

    /**
     * Function to notify the {@link com.psx.commons.RxBus} that logout process has been <b>initiated</b>.
     * This function sends an {@link com.psx.commons.ExchangeObject.EventExchangeObject} on current {@link com.psx.commons.RxBus}
     * so that any activities subscribed to it can be notified of the event.
     *
     * @see com.psx.commons.ExchangeObject.EventExchangeObject#EventExchangeObject(Modules, Modules, CustomEvents)
     * @see CustomEvents#LOGOUT_INITIATED
     */
    private static void notifyLogoutInitiated() {
        Timber.i("Logout initiated");
        ExchangeObject.EventExchangeObject eventExchangeObject = new ExchangeObject.EventExchangeObject(Modules.MAIN_APP, Modules.ANCILLARY_SCREENS, CustomEvents.LOGOUT_INITIATED);
        mainApplication.getEventBus().send(eventExchangeObject);
    }

    /**
     * Function to notify the {@link com.psx.commons.RxBus} that logout process has been <b>completed</b>.
     * This function sends an {@link com.psx.commons.ExchangeObject.EventExchangeObject} on current {@link com.psx.commons.RxBus}
     * so that any activities subscribed to it can be notified of the event.
     *
     * @see com.psx.commons.ExchangeObject.EventExchangeObject#EventExchangeObject(Modules, Modules, CustomEvents)
     * @see CustomEvents#LOGOUT_COMPLETED
     */
    private static void notifyLogoutCompleted() {
        Timber.i("Logout completed");
        ExchangeObject.EventExchangeObject eventExchangeObject = new ExchangeObject.EventExchangeObject(Modules.MAIN_APP, Modules.ANCILLARY_SCREENS, CustomEvents.LOGOUT_COMPLETED);
        mainApplication.getEventBus().send(eventExchangeObject);
    }
}
