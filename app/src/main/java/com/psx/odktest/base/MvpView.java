package com.psx.odktest.base;

import android.content.Context;

public interface MvpView extends ODKTestActivity {

    Context getActivityContext();

    void showSnackbar(String message, int duration);
}
