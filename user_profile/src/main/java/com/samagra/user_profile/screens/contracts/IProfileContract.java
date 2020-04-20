package com.samagra.user_profile.screens.contracts;

import android.content.Context;

import androidx.annotation.NonNull;

import com.samagra.user_profile.models.UserProfileElement;

import java.util.ArrayList;


public interface IProfileContract {

    /**
     *  @param context
     * @param userProfileElements
     * @param fusionAuthApiKey
     */
    void launchProfileActivity(@NonNull Context context, ArrayList<UserProfileElement> userProfileElements,
                               String fusionAuthApiKey);

}