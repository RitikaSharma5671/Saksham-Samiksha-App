package com.samagra.user_profile;

import androidx.annotation.NonNull;

import com.samagra.commons.MainApplication;


public class ProfileSectionDriver {
    public static String USER_ID ;
    public static MainApplication mainApplication = null;
    public static String BASE_API_URL;
    public static String applicationID;
    public static String FUSION_AUTH_API_KEY;
    public static String SEND_OTP_URL;
    public static String RESET_PASSWORD_URL;

    public static void init(@NonNull MainApplication mainApplication, String BASE_URL, String applicationID,
                            @NonNull String SEND_OTP_URL, @NonNull String RESET_PASSWORD_URL,
                            @NonNull String FUSION_AUTH_API_KEY, @NonNull String USER_ID) {
        ProfileSectionDriver.mainApplication = mainApplication;
        ProfileSectionDriver.BASE_API_URL = BASE_URL;
        ProfileSectionDriver.applicationID = applicationID;
        ProfileSectionDriver.SEND_OTP_URL = SEND_OTP_URL;
        ProfileSectionDriver.RESET_PASSWORD_URL = RESET_PASSWORD_URL;
        ProfileSectionDriver.FUSION_AUTH_API_KEY = FUSION_AUTH_API_KEY;
        ProfileSectionDriver.USER_ID = USER_ID;
    }

//    /**
//     * Function to launch the {@link ProfileActivity} which displays the user's profile.
//     *  @param context             - The current Activity's context.
//     * @param userProfileElements - A list of {@link UserProfileElement} that depict an individual
//     */
//    public static void launchProfileActivity(@NonNull Context context, ArrayList<UserProfileElement> userProfileElements, String fusionAuthApiKey) {
//        checkValidConfig();
//        Intent intent = new Intent(context, ProfileActivity.class);
//        intent.putParcelableArrayListExtra("config", userProfileElements);
//        intent.putExtra("apiKey", fusionAuthApiKey);
//        intent.putExtra("applicationID", applicationID);
//        Grove.d("Profile screen about to be launched...");
//        context.startActivity(intent);
//    }


    private static void checkValidConfig() {

    }


}