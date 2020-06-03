package com.samagra.parent;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;

/**
 * These are constants required by the app module. All values declared should be public static and the constants should
 * preferably be final constants.
 *
 * @author Pranav Sharma
 */
public class AppConstants {
    public static final String PREF_FILE_NAME = "SAMAGRA_PREFS";
    public static final String BASE_API_URL = "https://www.auth.saksham.staging.samagra.io";
    public static final String ROOT = Environment.getExternalStorageDirectory()
            + File.separator + "odk";
    public static final String FILE_PATH =  Environment.getExternalStorageDirectory()
            + File.separator + "odk" + "/saksham_data_json.json";
    public static final String SEND_OTP_URL = "https://relay.saksham.staging.samagra.io/shiksha-saathi/";
    public static final String UPDATE_PASSWORD_URL = "https://relay.saksham.staging.samagra.io/shiksha-saathi/";
    public static final String SENDER_EMAIL_ID = "";
    public static final String RECEIVER_EMAIL_ID = "";
    public static final String APPLICATION_ID = "dd56e032-fb78-438b-8257-563f82a9f293";
    public static String ABOUT_WEBSITE_LINK = "https://samagra-development.github.io/docs/";
    public static String ABOOUT_FORUM_LINK = "https://samagra-development.github.io/docs/";

}
