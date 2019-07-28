package com.psx.ancillaryscreens;

import android.content.Context;
import android.content.Intent;

import com.psx.ancillaryscreens.screens.login.LoginActivity;
import com.psx.commons.CommonUtilities;
import com.psx.commons.MainApplication;

public class AncillaryScreensDriver {
    public static MainApplication mainApplication = null;

    public static void init(MainApplication mainApplication) {
        AncillaryScreensDriver.mainApplication = mainApplication;
    }

    public static void launchLoginScreen(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        CommonUtilities.startActivityAsNewTask(intent, context);
    }
}
