package com.example.student_details.contracts;

import android.app.Application;

import com.example.student_details.db.StudentManagementRealmModule;
import com.samagra.commons.MainApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class StudentDetailsComponentManager {

    public static IStudentDetailsContract iStudentDetailsContract;
    public static String API_KEY;
    public static String APPLICATION_ID;
    public static String BASE_API_URL;

    /**
     * @param profileContractImpl IStudentDetailsContract Instance
     * @param application Application Instance
     * @param baseApiUrl
     */
    public static void registerStudentModule(IStudentDetailsContract profileContractImpl, Application application, String apiKey, String applicationId, String baseApiUrl) {
        iStudentDetailsContract = profileContractImpl;
        StudentDetailsComponentManager.API_KEY = apiKey;
        StudentDetailsComponentManager.APPLICATION_ID = applicationId;
        StudentDetailsComponentManager.BASE_API_URL = baseApiUrl;
        initialiseRealm(application);
    }

    private static void initialiseRealm(Application application) {
        Realm.init(application);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("StudentDetailsComponentManager")
                .modules(new StudentManagementRealmModule())
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(2)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }

}