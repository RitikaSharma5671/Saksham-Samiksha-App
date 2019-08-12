package com.psx.commons.TaskScheduler;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkRequest;
import androidx.work.Worker;

import com.google.gson.Gson;
import com.psx.commons.Constants;
import com.psx.commons.InitializationException;
import com.psx.commons.MainApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

public class Manager {

    private static MainApplication mainApplication = null;
    private static Set<String> incompleteTasksArrayList = new ArraySet<>();
    private static final String INCOMPLETE_TASK_LIST = "incomplete_work";

    public static void init(MainApplication mainApplication) {
        Manager.mainApplication = mainApplication;
        loadIncompleteTasksArrayList(mainApplication.getCurrentApplication().getApplicationContext());
    }

    static MainApplication getMainApplication() {
        if (mainApplication == null)
            throw new InitializationException(Manager.class, Manager.class.getCanonicalName() + " not initialised.\nPlease call init method.");
        return mainApplication;
    }

    static boolean isTaskAlreadyInPrefs(Context context, UUID uuid) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return !Objects.requireNonNull(sharedPreferences.getString(uuid.toString(), "null")).equals("null");
    }

    static ArrayList<WorkRequest> enqueueAllIncompleteTasks() {
        SharedPreferences sharedPreferences = mainApplication.getCurrentApplication()
                .getApplicationContext().getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        ArrayList<WorkRequest> workRequests = new ArrayList<>();
        for (String uuid : incompleteTasksArrayList) {
            String json = sharedPreferences.getString(uuid, "");
            if (json != null && !json.equals("")) {
                SavedTask savedTask = new Gson().fromJson(json, SavedTask.class);
                workRequests.add(savedTask.convertToWorkRequest());
            } else {
                Timber.wtf("Trying to access unsaved task");
            }
        }
        return workRequests;
    }

    private static void loadIncompleteTasksArrayList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        incompleteTasksArrayList = sharedPreferences.getStringSet(INCOMPLETE_TASK_LIST, new ArraySet<>());
    }

    private static void updateIncompleteTasksArrayList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(INCOMPLETE_TASK_LIST, incompleteTasksArrayList);
        editor.apply();
        loadIncompleteTasksArrayList(context);
    }

    static class SavedTask implements Serializable {

        private Data data;
        private String strUUID;
        private String className;

        static SavedTask createSavedTaskFromWorkInfo(WorkInfo workInfo, Class clazz) {
            return new SavedTask(workInfo.getOutputData(), workInfo.getId().toString(), clazz.getCanonicalName());
        }

        private SavedTask(@Nullable Data data, String strUUID, String className) {
            this.data = data; // Doubtful if this will serialize
            this.strUUID = strUUID;
            this.className = className;
        }

        WorkRequest convertToWorkRequest() {
            try {
                return new OneTimeWorkRequest.Builder(
                        Class.forName(className).asSubclass(Worker.class))
                        .setInputData(data)
                        .build();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        void saveTaskInSharedPrefs(Context context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = new Gson().toJson(this);
            editor.putString(this.strUUID, json);
            editor.apply();
            addToIncompleteTaskArrayList();
        }

        static void clearSavedTaskFromSharedPrefs(Context context, String UUID) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(UUID);
            editor.apply();
            removeFromIncompleteTaskArrayList(UUID);
        }

        private void addToIncompleteTaskArrayList() {
            incompleteTasksArrayList.add(strUUID);
            updateIncompleteTasksArrayList(mainApplication.getCurrentApplication().getApplicationContext());
        }

        private static void removeFromIncompleteTaskArrayList(String UUID) {
            incompleteTasksArrayList.remove(UUID);
            updateIncompleteTasksArrayList(mainApplication.getCurrentApplication().getApplicationContext());
        }
    }
}

