package com.psx.ancillaryscreens;

import androidx.annotation.NonNull;

import com.psx.ancillaryscreens.screens.about.AboutActivity;

import org.jetbrains.annotations.NotNull;

public class InvalidConfigurationException extends RuntimeException {

    private final String message = "InvalidConfigurationException. Did you forget Configuring this activity ?";
    private String detailedMessage;
    private Class clazz;

    public InvalidConfigurationException(@NonNull Class clazz) {
        this.clazz = clazz;
    }

    @NotNull
    @Override
    public String toString() {
        return message + "\n Detailed Message: " + detailedMessage;
    }

    public String getDetailedMessage() {
        if (clazz.getCanonicalName().equals(AboutActivity.class.getCanonicalName())) {
            detailedMessage = "AboutActivity is not initialized. You need to pass AboutBundle to configure AboutActivity";
        } else {
            detailedMessage = "No further details Available.";
        }
        return detailedMessage;
    }
}
