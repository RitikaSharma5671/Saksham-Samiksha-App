package com.psx.commons;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

/**
 * This class contains the common utilities that can be used independently in any module using this library.
 * All the functions in this class must be public static.
 */
public class CommonUtilities {
    /**
     * Starts activity as a new task. This means all the activities in the current Task will be removed.
     * Basically, clears the back stack.
     *
     * @param intent  - The intent repsonsible for the new activity
     * @param context - The context for the current Activity.
     */
    public static void startActivityAsNewTask(Intent intent, @NonNull Context context) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
}
