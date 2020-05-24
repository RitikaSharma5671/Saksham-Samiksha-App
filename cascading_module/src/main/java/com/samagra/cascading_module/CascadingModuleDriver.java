package com.samagra.cascading_module;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.samagra.cascading_module.ui.SearchActivity;
import com.samagra.commons.MainApplication;

/**
 * The driver class for this module, any screen that needs to be launched from outside this module, should be
 * launched using this class.
 * Note: It is essential that you call the {@link CascadingModuleDriver#init(MainApplication, String, String)} to initialise
 * the class prior to using it else an Exception will be thrown.
 *
 * @author Pranav Sharma
 */
public class CascadingModuleDriver {
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 123;
    public static String FILE_PATH;
    public static MainApplication application;
    public static String ROOT;

    /**
     * @param mainApplication
     * @param FILE_PATH
     * @param root
     */
    public static void init(@NonNull MainApplication mainApplication, @NonNull String FILE_PATH, String root) {
        CascadingModuleDriver.FILE_PATH = FILE_PATH;
        CascadingModuleDriver.ROOT = root;
        CascadingModuleDriver.application = mainApplication;
    }

    public static void launchSearchView(Context context, String path, Activity activity) {
        Intent intent = new Intent(context, SearchActivity.class);
        activity.startActivityForResult(intent, CascadingModuleDriver.SEARCH_ACTIVITY_REQUEST_CODE);
//        context.startActivityF(intent);
    }


}
