package com.psx.commons.TaskScheduler;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.google.gson.Gson;
import com.psx.commons.Constants;
import com.psx.commons.InitializationException;
import com.psx.commons.MainApplication;

import java.io.Serializable;

public class Manager {

    private static MainApplication mainApplication = null;

    public static void init(MainApplication mainApplication) {
        Manager.mainApplication = mainApplication;
    }

    public static MainApplication getMainApplication() {
        if (mainApplication == null)
            throw new InitializationException(Manager.class, Manager.class.getCanonicalName() + " not initialised.\nPlease call init method.");
        return mainApplication;
    }

    static class SavedTask implements Serializable {

        private Data data;
        private String strUUID;
        private String className;

        public static SavedTask createSavedTaskFromWorkInfo(WorkInfo workInfo, Class clazz) {
            return new SavedTask(workInfo.getOutputData(), workInfo.getId().toString(), clazz.getCanonicalName());
        }

        private SavedTask(@Nullable Data data, String strUUID, String className) {
            this.data = data;
            this.strUUID = strUUID;
            this.className = className;
        }

        private void saveTaskInSharedPrefs(Context context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = new Gson().toJson(this);
            editor.putString(this.strUUID, json);
            editor.apply();
        }
    }
}

