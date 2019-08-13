package com.psx.commons.TaskScheduler;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;

import com.psx.commons.InitializationException;

import java.util.UUID;

import timber.log.Timber;

public class ScheduledOneTimeWork implements ScheduledTask {

    private OneTimeWorkRequest oneTimeWorkRequest;
    private Class clazz;

    public static ScheduledOneTimeWork from(Class clazz) {
        if (Worker.class.isAssignableFrom(clazz)) {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(clazz).build();
            return new ScheduledOneTimeWork(workRequest, clazz);
        } else {
            throw new InitializationException(ScheduledOneTimeWork.class, "Unable to instantiate class. Trying to create ScheduledTask from a class other than Worker class.");
        }
    }

    public static ScheduledOneTimeWork from(Class clazz, Data inputDataForWorker) {
        if (Worker.class.isAssignableFrom(clazz)) {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(clazz)
                    .setInputData(inputDataForWorker)
                    .build();
            return new ScheduledOneTimeWork(workRequest, clazz);
        } else {
            throw new InitializationException(ScheduledOneTimeWork.class, "Unable to instantiate class. Trying to create ScheduledTask from a class other than Worker class.");
        }
    }

    private ScheduledOneTimeWork(OneTimeWorkRequest oneTimeWorkRequest, Class clazz) {
        this.oneTimeWorkRequest = oneTimeWorkRequest;
        this.clazz = clazz;
    }

    @Override
    @Nullable
    public UUID getScheduledTaskId() {
        if (oneTimeWorkRequest != null)
            return oneTimeWorkRequest.getId();
        else return null;
    }

    @Override
    public void enqueueTask(Context context) {
        Timber.d("Enqueuing task");
        WorkManager workManager = WorkManager.getInstance(context);
        LiveData<WorkInfo> status = workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId());
        status.observe(Manager.getMainApplication(), workInfo -> {
            if (workInfo.getState().equals(WorkInfo.State.ENQUEUED)) {
                Timber.i("Task enqueued");
                if (!Manager.isTaskAlreadyInPrefs(context, workInfo.getId())) {
                    Manager.SavedTask.createSavedTaskFromWorkInfo(workInfo, clazz).saveTaskInSharedPrefs(context);
                }
            } else if (workInfo.getState().isFinished()) {
                Timber.i("Task finished");
                Manager.SavedTask.clearSavedTaskFromSharedPrefs(context, workInfo.getId().toString());
            }
        });
        workManager.enqueue(oneTimeWorkRequest);
    }

    @Override
    public void cancelTask(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        if (getScheduledTaskId() != null) {
            Timber.i("Task cancelled");
            workManager.getWorkInfoById(getScheduledTaskId()).cancel(true);
        } else {
            Timber.wtf("Trying to cancel a task that was never scheduled. How did you get this UUID ??!!");
        }
    }

}
