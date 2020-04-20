package org.odk.collect.android.contracts;

import com.samagra.commons.MainApplication;

public class FormsComponentManager {
    public static IFormManagementContract iFormManagementContract;

    /**
     *
     * @param formManagmentClassImpl
     */
    public static void registerFormManagementPackage(IFormManagementContract formManagmentClassImpl) {
        iFormManagementContract = formManagmentClassImpl;
    }

    /**
     * @param application            - Application Class Instance
     * @param baseAPIUrl             - Base API Url, which will be later used to make API calls.
     * @param formManagmentClassImpl - Instance of the Implementing class implementing the contract, which will further call the helper methods.
     */
    public static void registerFormManagementPackage(MainApplication application, String baseAPIUrl, IFormManagementContract formManagmentClassImpl) {
        iFormManagementContract = formManagmentClassImpl;
    }

}