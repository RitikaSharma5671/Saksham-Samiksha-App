package com.psx.odktest.base;

import android.content.Context;

public interface MvpView {
    void setupToolbar();

    Context getActivityContext();

    void showSnackbar(String message, int duration);
}
