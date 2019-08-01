package com.psx.odktest;

import java.util.HashMap;

public class AppConstants {
    public static final String PREF_FILE_NAME = "SAMAGRA_PREFS";
    public static final String BASE_API_URL = "http://www.auth.samagra.io:9011";

    public static final HashMap<String, String> FORM_LIST = new HashMap<String, String>() {{
        put("CHT_V1", "Class 1st to 5th CHT School Visit Form");
        put("BRCC_V1", "Class 1st to 8th School Visit Form");
        put("BPO_V1", "Class 9th to 12th School Visit Form");
    }};
}
