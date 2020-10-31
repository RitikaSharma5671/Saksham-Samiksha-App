package com.samagra.ancillaryscreens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.samagra.ancillaryscreens.data.network.BackendCallHelperImpl;
import com.samagra.ancillaryscreens.models.AboutBundle;
import com.samagra.ancillaryscreens.screens.about.AboutActivity;
import com.samagra.ancillaryscreens.screens.login.LoginActivity;
import com.samagra.ancillaryscreens.screens.profile.ProfileActivity;
import com.samagra.ancillaryscreens.screens.profile.UserProfileElement;
import com.samagra.ancillaryscreens.screens.tutorials.TutorialActivity;
import com.samagra.commons.CommonUtilities;
import com.samagra.commons.Constants;
import com.samagra.commons.CustomEvents;
import com.samagra.commons.ExchangeObject;
import com.samagra.commons.MainApplication;
import com.samagra.commons.Modules;
import com.samagra.grove.logging.Grove;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * The driver class for this module, any screen that needs to be launched from outside this module, should be
 * launched using this class.
 * Note: It is essential that you call the {@link AncillaryScreensDriver#init(MainApplication, String, String, String, String)} to initialise
 * the class prior to using it else an {@link InvalidConfigurationException} will be thrown.
 *
 * @author Pranav Sharma
 */
public class AncillaryScreensDriver {
    public static String UPDATE_PASSWORD_URL;
    public static MainApplication mainApplication = null;
    public static String BASE_API_URL;
    public static String SEND_OTP_URL;
    public static String APPLICATION_ID;
    private static String USER_ID;

    /**
     *
     * @param mainApplication MainApplication Instance
     * @param BASE_URL - String Base API URL
     * @param SEND_OTP_URL - String SEND OTP URL
     * @param UPDATE_PASSWORD_URL - String Update Password URL
     * @param APPLICATION_ID - String Application ID
     */
    public static void init(@NonNull MainApplication mainApplication, @NonNull String BASE_URL, @NonNull String SEND_OTP_URL,
                            @NonNull String UPDATE_PASSWORD_URL, @NonNull String APPLICATION_ID) {
        AncillaryScreensDriver.mainApplication = mainApplication;
        AncillaryScreensDriver.BASE_API_URL = BASE_URL;
        AncillaryScreensDriver.SEND_OTP_URL = SEND_OTP_URL;
        AncillaryScreensDriver.UPDATE_PASSWORD_URL = UPDATE_PASSWORD_URL;
        AncillaryScreensDriver.APPLICATION_ID = APPLICATION_ID;
    }

    /**
     * Function to launch the login screen from this module. This function starts the {@link LoginActivity} as a new task,
     * clearing everything else in the activity back-stack.
     *
     * @param context - The current Activity's context.
     */
    public static void launchLoginScreen(@NonNull Context context) {
        checkValidConfig();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
//        CommonUtilities.startActivityAsNewTask(intent, context);
    }

    /**
     * Function to launch the about activity from this module.
     *
     * @param context     - The current Activity's context.
     * @param aboutBundle - A Java object containing values to configure {@link AboutActivity}'s appearance.
     * @see AboutBundle#AboutBundle(String, String, String, int, int, int)
     */
    public static void launchAboutActivity(@NonNull Context context, @NonNull AboutBundle aboutBundle) {
        checkValidConfig();
        Intent intent = new Intent(context, AboutActivity.class);
        intent.putExtra("config", aboutBundle.aboutExchangeBundle);
        context.startActivity(intent);
    }

    /**
     * Function to launch the about activity from this module.
     *
     * @param context     - The current Activity's context.
     * @param videoDetailsBundle - A Java object containing values to configure {@link AboutActivity}'s appearance.
     * @see AboutBundle#AboutBundle(String, String, String, int, int, int)
     */
    public static void launchTutorialActivity(@NonNull Context context, @NonNull Bundle videoDetailsBundle) {
        checkValidConfig();
        Intent intent = new Intent(context, TutorialActivity.class);
        intent.putExtra("video_details_bundle", videoDetailsBundle);
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
        Grove.d("Inside performLogout() method....");
        checkValidConfig();
        notifyLogoutInitiated();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString("FCM.token", "");
        if (CommonUtilities.isNetworkAvailable(context)) {
            try {
                makeRemoveTokenApiCall(context);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(context, "No Internet Connection. Please connect to internet and try again later", Toast.LENGTH_LONG).show();
    }

    public static void launchProfileActivity(Context context, ArrayList<UserProfileElement> profileElements, String fetchUserID) {
        checkValidConfig();
        // Intent intent = new Intent(context, ProfileActivity.class);

        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putParcelableArrayListExtra("config", profileElements);
        USER_ID = fetchUserID;
        context.startActivity(intent);
    }



    /**
     * This function makes an API call to get the current User Object stored at the backend. This function is the first part
     * of a 2-step process to logout the user. The object retrieved by this API call is then edited to remove
     * the FCMToken from it. This is done to prevent logged out users from receiving notifications.
     *
     * @param context - The current Activity Context.
     * @see AncillaryScreensDriver#removeFCMTokenFromObject(JSONObject) - This edits the JSONObject retrieved by removing the FCM token.
     */
    private static void makeRemoveTokenApiCall(@NonNull Context context) throws JSONException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString("token", "");
        BackendCallHelperImpl.getInstance()
                .performLogoutApiCall(sharedPreferences.getString("refreshToken", ""), userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Grove.d("OnSubscribe make Remove token api call onSubscribe() called");
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        String appLanguage = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.APP_LANGUAGE_KEY, "en");
                        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
                        Grove.d("OnSuccess() make Remove token api makeRemoveTokenApiCall() call invoked");
                        Grove.d("Removed FCM Token, from the user data");
                        logoutUserLocally(context, appLanguage);
                        notifyLogoutCompleted();
                        Intent intent = new Intent(context, LoginActivity.class);
                        CommonUtilities.startActivityAsNewTask(intent, context);                    }

                    @Override
                    public void onError(Throwable e) {
                        notifyLogoutCompleted();
                        Grove.e("onError() called for makeRemoveTokenApiCall() with error Exception: " + e.getMessage());
                        Toast.makeText(context, context.getResources().getString(R.string.unable_to_log_out), Toast.LENGTH_SHORT).show();
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
        Grove.d("Removing FCM Token from %s", jsonObject.toString());
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
     * This function updates the login related flags set by the {@link LoginActivity} to reflect that the user is now
     * logged out.
     *
     * @param context - The current Activity/Application context.
     * @param appLanguage - App Language Locale
     */
    private static void logoutUserLocally(@NonNull Context context, String appLanguage) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putString(Constants.APP_LANGUAGE_KEY, appLanguage);
        editor.remove("updatedMappingThroughFirebase2");
        editor.remove("formVersion");
        editor.apply();
    }

    /**
     * Function to notify the {@link com.samagra.commons.RxBus} that logout process has been <b>initiated</b>.
     * This function sends an {@link com.samagra.commons.ExchangeObject.EventExchangeObject} on current {@link com.samagra.commons.RxBus}
     * so that any activities subscribed to it can be notified of the event.
     *
     * @see com.samagra.commons.ExchangeObject.EventExchangeObject#EventExchangeObject(Modules, Modules, CustomEvents)
     * @see CustomEvents#LOGOUT_INITIATED
     */
    private static void notifyLogoutInitiated() {
        Grove.d("Logout initiated Event Triggered");
        ExchangeObject.EventExchangeObject eventExchangeObject = new ExchangeObject.EventExchangeObject(Modules.MAIN_APP, Modules.ANCILLARY_SCREENS, CustomEvents.LOGOUT_INITIATED);
        mainApplication.getEventBus().send(eventExchangeObject);
    }

    /**
     * Function to notify the {@link com.samagra.commons.RxBus} that logout process has been <b>completed</b>.
     * This function sends an {@link com.samagra.commons.ExchangeObject.EventExchangeObject} on current {@link com.samagra.commons.RxBus}
     * so that any activities subscribed to it can be notified of the event.
     *
     * @see com.samagra.commons.ExchangeObject.EventExchangeObject#EventExchangeObject(Modules, Modules, CustomEvents)
     * @see CustomEvents#LOGOUT_COMPLETED
     */
    private static void notifyLogoutCompleted() {
        Grove.d("Logout completed event triggered ");
        ExchangeObject.EventExchangeObject eventExchangeObject = new ExchangeObject.EventExchangeObject(Modules.MAIN_APP, Modules.ANCILLARY_SCREENS, CustomEvents.LOGOUT_COMPLETED);
        mainApplication.getEventBus().send(eventExchangeObject);
    }

    /**
     * Function to check if the mainApplication is initialised indicating if {@link AncillaryScreensDriver#init(MainApplication, String, String, String, String)} is called or not.
     * If not, it throws {@link InvalidConfigurationException}
     *
     * @throws InvalidConfigurationException - This Exception means that the module is not configured by the user properly. The exception generates
     *                                       detailed message depending on the class that throws it.
     */
    private static void checkValidConfig() {
        if (mainApplication == null)
            throw new InvalidConfigurationException(AncillaryScreensDriver.class);
    }
}
