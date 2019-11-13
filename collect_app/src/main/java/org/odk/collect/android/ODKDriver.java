package org.odk.collect.android;

import android.content.Context;
import android.content.Intent;

import com.samagra.commons.MainApplication;

import org.odk.collect.android.activities.InstanceUploaderListActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.DownloadFormListUtils;

public class ODKDriver {
    public static final String LAUNCH_INTENT_ACTION = "com.psx.odk.START_ODK_APP";
    private static int splashScreenImageRes = R.drawable.notes;
    private static boolean isUsingCustomTheme = false;
    private static int customThemeId = -1;
    private static int customThemeId_FormEntry = -1;
    private static int customThemeId_Settings = -1;
    private static long toolbarIconResId = -1;
    private static boolean modifyToolbarIcon = false;
    public static MainApplication applicationInstance = null;

    public static void init(MainApplication applicationInstance, int splashScreenImageRes) {
        ODKDriver.applicationInstance = applicationInstance;
        ODKDriver.splashScreenImageRes = splashScreenImageRes;
    }

    public static void init(MainApplication applicationInstance, int splashScreenImageRes, int customThemeId, int customThemeId_FormEntry, int customThemeId_Settings) {
        isUsingCustomTheme = true;
        ODKDriver.customThemeId = customThemeId;
        ODKDriver.customThemeId_FormEntry = customThemeId_FormEntry;
        ODKDriver.customThemeId_Settings = customThemeId_Settings;
        init(applicationInstance, splashScreenImageRes);
    }

    public static void init(MainApplication applicationInstance, int splashScreenImageRes, int customThemeId, int customThemeId_FormEntry, int customThemeId_Settings, long toolbarIconResId) {
        init(applicationInstance, splashScreenImageRes, customThemeId, customThemeId_FormEntry, customThemeId_Settings);
        modifyToolbarIcon = true;
        ODKDriver.toolbarIconResId = toolbarIconResId;
    }

    public static void launchInstanceUploaderListActivity(Context context) {
        Intent intent = new Intent(context, InstanceUploaderListActivity.class);
        context.startActivity(intent);
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

    public static DownloadFormListUtils getDownloadFormListUtils() {return Collect.getInstance().getComponent().downloadFormListUtils();}
}
