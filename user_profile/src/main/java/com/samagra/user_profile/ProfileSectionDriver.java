package com.samagra.user_profile;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.samagra.commons.MainApplication;
import com.samagra.user_profile.models.UserProfileElement;
import com.samagra.user_profile.screens.profile.ProfileActivity;

import java.util.ArrayList;

/**
 * The driver class for this module, any screen that needs to be launched from outside this module, should be
 * launched using this class.
 * Note: It is essential that you call the {@link ProfileSectionDriver#init(MainApplication, String, String, String, String)} to initialise
 * the class prior to using it else an {@link InvalidConfigurationException} will be thrown.
 *
 * @author Pranav Sharma
 */
public class ProfileSectionDriver {
    public static MainApplication mainApplication = null;
    public static String BASE_API_URL;
    static String applicationID;
    private static String SEND_OTP_URL;
    private static String RESET_PASSWORD_URL;

    public static void init(@NonNull MainApplication mainApplication, String BASE_URL, String applicationID, @NonNull String SEND_OTP_URL, @NonNull String RESET_PASSWORD_URL) {
        ProfileSectionDriver.mainApplication = mainApplication;
        ProfileSectionDriver.BASE_API_URL = BASE_URL;
        ProfileSectionDriver.applicationID = applicationID;
        ProfileSectionDriver.SEND_OTP_URL = SEND_OTP_URL;
        ProfileSectionDriver.RESET_PASSWORD_URL = RESET_PASSWORD_URL;
    }

    /**
     * Function to launch the {@link ProfileActivity} which displays the user's profile.
     *  @param context             - The current Activity's context.
     * @param userProfileElements - A list of {@link UserProfileElement} that depict an individual
     */
    public static void launchProfileActivity(@NonNull Context context, ArrayList<UserProfileElement> userProfileElements, String fusionAuthApiKey) {
        checkValidConfig();
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putParcelableArrayListExtra("config", userProfileElements);
        intent.putExtra("apiKey", fusionAuthApiKey);
        intent.putExtra("applicationID", applicationID);
        context.startActivity(intent);
    }

    /**
     * Function to check if the mainApplication is initialised indicating if {@link ProfileSectionDriver#init(MainApplication, String, String, String, String)} is called or not.
     * If not, it throws {@link InvalidConfigurationException}
     *
     * @throws InvalidConfigurationException - This Exception means that the module is not configured by the user properly. The exception generates
     *                                       detailed message depending on the class that throws it.
     */
    private static void checkValidConfig() {
        if (mainApplication == null)
            throw new InvalidConfigurationException(ProfileSectionDriver.class);
    }
}
