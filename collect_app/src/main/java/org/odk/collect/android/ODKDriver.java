package org.odk.collect.android;

public class ODKDriver {
    public static final String LAUNCH_INTENT_ACTION = "com.psx.odk.START_ODK_APP";
    private static int splashScreenImageRes = R.drawable.notes;
    private static boolean isUsingCustomTheme = false;
    private static int customThemeId = -1;
    private static int customThemeId_FormEntry = -1;
    private static int customThemeId_Settings = -1;
    private static long toolbarIconResId = -1;
    private static boolean modifyToolbarIcon = false;

    public static void init(int splashScreenImageRes) {
        ODKDriver.splashScreenImageRes = splashScreenImageRes;
    }

    public static void init(int splashScreenImageRes, int customThemeId, int customThemeId_FormEntry, int customThemeId_Settings) {
        isUsingCustomTheme = true;
        ODKDriver.customThemeId = customThemeId;
        ODKDriver.customThemeId_FormEntry = customThemeId_FormEntry;
        ODKDriver.customThemeId_Settings = customThemeId_Settings;
        init(splashScreenImageRes);
    }

    public static void init(int splashScreenImageRes, int customThemeId, int customThemeId_FormEntry, int customThemeId_Settings, long toolbarIconResId) {
        init(splashScreenImageRes, customThemeId, customThemeId_FormEntry, customThemeId_Settings);
        modifyToolbarIcon = true;
        ODKDriver.toolbarIconResId = toolbarIconResId;
    }

    public static int getSplashScreenImageRes() {
        return ODKDriver.splashScreenImageRes;
    }

    public static boolean isIsUsingCustomTheme() {
        return isUsingCustomTheme;
    }

    public static int getCustomThemeId() {
        return customThemeId;
    }

    public static int getCustomThemeId_FormEntry() {
        return customThemeId_FormEntry;
    }

    public static int getCustomThemeId_Settings() {
        return customThemeId_Settings;
    }

    public static long getToolbarIconResId() {
        return toolbarIconResId;
    }

    public static boolean isModifyToolbarIcon() {
        return modifyToolbarIcon;
    }
}
