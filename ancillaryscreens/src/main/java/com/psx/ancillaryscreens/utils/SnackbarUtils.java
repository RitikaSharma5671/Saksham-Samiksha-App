package com.psx.ancillaryscreens.utils;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;

public final class SnackbarUtils {
    private static final int DURATION_SHORT = 3500;
    private static final int DURATION_LONG = 5500;

    private SnackbarUtils() {

    }

    public static void showShortSnackbar(@NonNull View view, @NonNull String message) {
        showSnackbar(view, message, DURATION_SHORT);
    }

    public static void showLongSnackbar(@NonNull View view, @NonNull String message) {
        showSnackbar(view, message, DURATION_LONG);
    }

    /**
     * Displays snackbar with {@param message}
     * and multi-line message enabled.
     *
     * @param view    The view to find a parent from.
     * @param message The text to show.  Can be formatted text.
     */
    public static void showSnackbar(@NonNull View view, @NonNull String message, int duration) {
        if (message.isEmpty()) {
            return;
        }

        Snackbar snackbar = Snackbar.make(view, message.trim(), duration);
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setSingleLine(false);
        snackbar.show();
    }

    /**
     * Displays snackbar with {@param message}
     * and multi-line message enabled.
     *
     * @param view    The view to find a parent from.
     * @param message The text to show.  Can be formatted text.
     */
    public static void showSnackbar(@NonNull View view, @NonNull String message, int duration, String dismissText) {
        if (message.isEmpty()) {
            return;
        }

        Snackbar snackbar = Snackbar.make(view, message.trim(), duration)
                .setAction(dismissText.trim(), v -> {});
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setSingleLine(false);
        snackbar.show();
    }
}
