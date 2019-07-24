package com.psx.commons.base;

import android.content.Context;

public interface MvpView {

    Context getActivityContext();

    void showSnackbar(String message, int duration);
}
