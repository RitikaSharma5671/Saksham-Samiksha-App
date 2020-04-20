package com.samagra.user_profile.screens.contracts;

import java.io.File;

public interface OverrideUploadFileCallback {
    void sendAppLogsToServer(File file);
}