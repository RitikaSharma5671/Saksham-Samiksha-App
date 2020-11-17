package org.odk.collect.audiorecorder;

import android.content.Context;

public class AudioInitialiser {

    private static Context jjj;

    public static  void setiini(Context context) {
        jjj = context;
    }

    public static Context getJjj() {
        return jjj;
    }
}
