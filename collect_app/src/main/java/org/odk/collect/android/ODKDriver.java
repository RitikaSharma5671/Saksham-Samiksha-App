package org.odk.collect.android;

public class ODKDriver {
    public static final String LAUNCH_INTENT_ACTION = "com.psx.odk.START_ODK_APP";
    private static int splashScreenImageRes = R.drawable.notes;

    public static void init(int splashScreenImageRes) {
        ODKDriver.splashScreenImageRes = splashScreenImageRes;
    }

    public static int getSplashScreenImageRes() {
        return ODKDriver.splashScreenImageRes;
    }
}
