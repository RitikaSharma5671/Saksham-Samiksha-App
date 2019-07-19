package com.psx.odktest;

import com.psx.commons.Constants;

import java.util.HashMap;

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
     * @param goBackOnNavIconPress  - Should {@code finish()} be called if the navigation icon is pressed.
     */
    public static HashMap<String, Object> generateToolbarModificationObject(boolean navigationIconDisplay,
                                                                            int navigationIconResId,
                                                                            boolean goBackOnNavIconPress) {
        return new HashMap<String, Object>() {{
            put(Constants.CUSTOM_TOOLBAR_SHOW_NAVICON, navigationIconDisplay);
            put(Constants.CUSTOM_TOOLBAR_RESID_NAVICON, navigationIconResId);
            put(Constants.CUSTOM_TOOLBAR_BACK_NAVICON_CLICK, goBackOnNavIconPress);
        }};
    }
}
