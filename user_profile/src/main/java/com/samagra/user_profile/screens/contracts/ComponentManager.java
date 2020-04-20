package com.samagra.user_profile.screens.contracts;

import com.samagra.commons.MainApplication;
import com.samagra.user_profile.ProfileSectionDriver;

public class ComponentManager {
    public static IProfileContract iProfileContract;

    /**
     *
     * @param profileContractImpl
     * @param application
     * @param baseURL
     * @param applicationID
     */
    public static void registerProfilePackage(IProfileContract profileContractImpl, MainApplication application,
                                              String baseURL, String applicationID, String sendOTPUrl, String updatePasswordUrl) {
        ProfileSectionDriver.init(application, baseURL, applicationID, sendOTPUrl,  updatePasswordUrl);
        iProfileContract = profileContractImpl;
    }

}