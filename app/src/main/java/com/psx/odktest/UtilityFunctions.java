package com.psx.odktest;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.psx.commons.Constants;

import java.util.HashMap;

import timber.log.Timber;

/**
 * This class contains Utility function that can be accessed anywhere throughout the module 'app'.
 * All the functions in this class will be public and static.
 */
public class UtilityFunctions {

    /**
     * This function prepares a Map that contains the {@link android.app.ActionBar}'s properties and their values.
     * This overrides the default customization set in the {@link org.odk.collect.android.ODKDriver}'s init method.
     *
     * @param navigationIconDisplay - boolean to indicate if the navigation icon should be displayed.
     * @param navigationIconResId   - the resource id of the icon to be used as navigation icon. If {@code !navigationIconDisplay}, this has no effect.
     * @param title                 - the custom tilte of the screen. if null is passed, the title does not get affected.
     * @param goBackOnNavIconPress  - Should {@code finish()} be called if the navigation icon is pressed.
     */
    public static HashMap<String, Object> generateToolbarModificationObject(boolean navigationIconDisplay,
                                                                            int navigationIconResId,
                                                                            @Nullable String title,
                                                                            boolean goBackOnNavIconPress) {
        return new HashMap<String, Object>() {{
            put(Constants.CUSTOM_TOOLBAR_SHOW_NAVICON, navigationIconDisplay);
            put(Constants.CUSTOM_TOOLBAR_RESID_NAVICON, navigationIconResId);
            put(Constants.CUSTOM_TOOLBAR_BACK_NAVICON_CLICK, goBackOnNavIconPress);
            put(Constants.CUSTOM_TOOLBAR_TITLE, title);
        }};
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
