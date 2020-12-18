package com.samagra.grove.contracts;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.samagra.grove.logging.Grove;
import com.samagra.grove.logging.GroveUtils;
import com.samagra.grove.logging.LoggableApplication;

public class GroveLoggingComponentLauncher implements IGroveLoggingComponent {

    @Override
    public void initializeLoggingComponent(
            Application application,
            LoggableApplication applicationInstance, Context context,
            ErrorActivityHandler errorActivityHandler,
            boolean isUCEHEnabled,
            boolean isHyperlogEnabled,
            String domainID,
            String senderEmailID,
            String receiverEmailID
    ) {
        Grove.init(applicationInstance, context, isHyperlogEnabled, domainID);
    }

    @Override
    public void uploadLogFile(String apiURL, final Context context, String authToken, boolean isOverrideMethod, OverrideUploadFileCallback overrideUploadFileCallback) {

    }


    @Override
    public void setAppUserName(String userName) {
        if (TextUtils.isEmpty(userName)) userName = "";
        GroveUtils.setUserName(userName);
    }

    @Override
    public void setAppUserData(String userData) {
        if (TextUtils.isEmpty(userData)) userData = "";
        GroveUtils.setUserData(userData);
    }
}