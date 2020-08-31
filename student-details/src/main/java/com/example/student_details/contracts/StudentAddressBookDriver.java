package com.example.student_details.contracts;

import android.content.Context;

import androidx.annotation.NonNull;

import com.samagra.commons.MainApplication;

import io.realm.Realm;

public class StudentAddressBookDriver {
    public static MainApplication mainApplication = null;
    public static String BASE_API_URL;

    public static void init(@NonNull MainApplication mainApplication, @NonNull String BASE_URL) {
        StudentAddressBookDriver.mainApplication = mainApplication;
        StudentAddressBookDriver.BASE_API_URL = BASE_URL;
    }

    /**
     * Function to launch the login screen from this module. This function starts the {@link } as a new task,
     * clearing everything else in the activity back-stack.
     */
    public static void deleteAllData(Context context) {
        Realm.init(context);
        Realm realm = getRealm();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    public static Realm getRealm(){
        return null;
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
//                .name(Application.class.getName())
//                .modules(new StudentManagementRealmModule())
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        return Realm.getInstance(realmConfiguration);
    }
}
