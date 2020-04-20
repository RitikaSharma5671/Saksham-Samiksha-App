package com.samagra.user_profile.screens.change_password;

public interface ActionListener {
    /**
     * The operation succeeded
     */
    void onSuccess();

    /**
     * The operation failed
     *
     * @param exception which caused the failure
     */
    void onFailure(Exception exception);
}
