package com.samagra.parent;

import android.os.Environment;

import org.odk.collect.android.application.Collect;

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
    public static final String BASE_API_URL = "http://www.auth.samagra.io:9011";
    public static final String ROOT = Collect.getInstance().getStoragePathProvider().getScopedStorageRootDirPath();
    public static final String FILE_PATH =  Collect.getInstance().getStoragePathProvider().getScopedStorageRootDirPath() + "/saksham_data_json.json";
    public static final String SEND_OTP_URL = "http://142.93.208.135:8080/shiksha-saathi/";
    public static final String UPDATE_PASSWORD_URL = "http://142.93.208.135:8080/shiksha-saathi/";
    public static final String SENDER_EMAIL_ID = "";
    public static final String RECEIVER_EMAIL_ID = "";
    public static final String APPLICATION_ID = "a664a16a-95dd-41fc-9b9b-e45fb49cf128";
    public static String ABOUT_WEBSITE_LINK = "https://samagra-development.github.io/docs/";
    public static String ABOOUT_FORUM_LINK = "https://samagra-development.github.io/docs/";
}
