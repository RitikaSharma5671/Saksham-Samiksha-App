package com.psx.commons.TaskScheduler;

import android.annotation.SuppressLint;
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

    public static void enqueueAllIncompleteTasks(@Nullable Context context) {
        Timber.d("Enqueuing All Tasks");
        SharedPreferences sharedPreferences = mainApplication.getCurrentApplication()
                .getApplicationContext().getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        for (String uuid : incompleteTasksArrayList) {
            String json = sharedPreferences.getString(uuid, "");
            if (json != null && !json.equals("")) {
                SavedTask savedTask = new Gson().fromJson(json, SavedTask.class);
                ScheduledOneTimeWork scheduledOneTimeWork = savedTask.convertToScheduledOneTimeWork();
                updateTaskUuidInSharedPrefs(savedTask, Objects.requireNonNull(scheduledOneTimeWork.getScheduledTaskId()).toString());
                if (context != null)
                    scheduledOneTimeWork.enqueueTask(context);
                else
                    savedTask.convertToScheduledOneTimeWork().enqueueTask(mainApplication.getCurrentApplication().getApplicationContext());
                Timber.e("Task with id %s enqueued.", savedTask.convertToWorkRequest().getId());
            } else {
                Timber.wtf("Trying to access unsaved task");
            }
        }
        Timber.i("All Tasks enqueued");
    }

    static MainApplication getMainApplication() {
        if (mainApplication == null)
            throw new InitializationException(Manager.class, Manager.class.getCanonicalName() + " not initialised.\nPlease call init method.");
        return mainApplication;
    }

    static boolean isTaskAlreadyInPrefs(UUID uuid) {
        SharedPreferences sharedPreferences = mainApplication.getCurrentApplication()
                .getApplicationContext()
                .getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return !Objects.requireNonNull(sharedPreferences.getString(uuid.toString(), "null")).equals("null");
    }

    private static void updateTaskUuidInSharedPrefs(SavedTask oldTask, String newUUID) {
        if (incompleteTasksArrayList.contains(oldTask.strUUID)) {
            incompleteTasksArrayList.remove(oldTask.strUUID);
            incompleteTasksArrayList.add(newUUID);
            updateIncompleteTasksArrayList(mainApplication.getCurrentApplication().getApplicationContext());
            SavedTask.clearSavedTaskFromSharedPrefs(oldTask.strUUID);
            oldTask.strUUID = newUUID;
            oldTask.saveTaskInSharedPrefs();
        } else {
            Timber.wtf("incompleteTasksArrayList does not contain the UUID you are trying to update");
        }
    }

    private static void loadIncompleteTasksArrayList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        incompleteTasksArrayList = sharedPreferences.getStringSet(INCOMPLETE_TASK_LIST, new ArraySet<>());
        System.out.println("Updated List is " + incompleteTasksArrayList);
    }

    @SuppressLint("ApplySharedPref")
    private static void updateIncompleteTasksArrayList(Context context) {
        Timber.d("Updating incomplete ArrayList");
        System.out.println("ARRAY LIST IS " + incompleteTasksArrayList.toString());
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(INCOMPLETE_TASK_LIST);
        editor.commit();
        editor.putStringSet(INCOMPLETE_TASK_LIST, incompleteTasksArrayList);
        editor.commit();
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

        ScheduledOneTimeWork convertToScheduledOneTimeWork() {
            try {
                if (data != null)
                    return ScheduledOneTimeWork.from(Class.forName(className).asSubclass(Worker.class), data);
                else
                    return ScheduledOneTimeWork.from(Class.forName(className).asSubclass(Worker.class));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
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

        void saveTaskInSharedPrefs() {
            Timber.d("Saving Task in SharedPreferences");
            SharedPreferences sharedPreferences = mainApplication.getCurrentApplication()
                    .getApplicationContext()
                    .getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = new Gson().toJson(this);
            editor.putString(this.strUUID, json);
            editor.apply();
            addToIncompleteTaskArrayList();
        }

        static void clearSavedTaskFromSharedPrefs(String UUID) {
            Timber.d("Clearing Task with UUID %s from Preferences", UUID);
            SharedPreferences sharedPreferences = mainApplication.getCurrentApplication()
                    .getApplicationContext()
                    .getSharedPreferences(Constants.WORK_MANAGER_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(UUID);
            editor.apply();
            removeFromIncompleteTaskArrayList(UUID);
        }

        private void addToIncompleteTaskArrayList() {
            if (!incompleteTasksArrayList.contains(strUUID)) {
                Timber.d("Adding to incomplete ArrayList");
                incompleteTasksArrayList.add(strUUID);
                updateIncompleteTasksArrayList(mainApplication.getCurrentApplication().getApplicationContext());
            }
        }

        private static void removeFromIncompleteTaskArrayList(String UUID) {
            if (incompleteTasksArrayList.contains(UUID)) {
                incompleteTasksArrayList.remove(UUID);
                updateIncompleteTasksArrayList(mainApplication.getCurrentApplication().getApplicationContext());
            } else {
                Timber.wtf("incompleteTasksArrayList does not contain UUID %s", UUID);
            }
        }
    }
}

