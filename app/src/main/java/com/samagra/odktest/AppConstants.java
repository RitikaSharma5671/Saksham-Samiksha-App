package com.samagra.odktest;

import java.util.HashMap;

/**
 * These are constants required by the app module. All values declared should be public static and the constants should
 * preferably be final constants.
 *
 * @author Pranav Sharma
 */
public class AppConstants {
    public static final String PREF_FILE_NAME = "SAMAGRA_PREFS";
    static final String BASE_API_URL = "http://www.auth.samagra.io:9011";
    public static String ABOUT_WEBSITE_LINK = "http://139.59.71.154:3000/public/dashboard/b5bab1e2-7e46-4134-b065-7d62cc4d70d0";
    public static String ABOOUT_FORUM_LINK = "http://samagragovernance.in/blog/";

    public static final HashMap<String, String> FORM_LIST = new HashMap<String, String>() {{
        put("CHT_V1", "Class 1st to 5th CHT School Visit Form");
        put("BRCC_V1", "Class 1st to 8th School Visit Form");
        put("BPO_V1", "Class 9th to 12th School Visit Form");
    }};
}
