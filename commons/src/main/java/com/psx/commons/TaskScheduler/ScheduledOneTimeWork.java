package com.psx.commons.TaskScheduler;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;

import java.util.UUID;

public class ScheduledOneTimeWork implements ScheduledTask {

    private OneTimeWorkRequest oneTimeWorkRequest;

    public static ScheduledOneTimeWork from(Worker worker) {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(worker.getClass()).build();
        return new ScheduledOneTimeWork(workRequest);
    }

    public static ScheduledOneTimeWork from(Worker worker, Data inputDataForWorker) {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(worker.getClass())
                .setInputData(inputDataForWorker)
                .build();
        return new ScheduledOneTimeWork(workRequest);
    }

    private ScheduledOneTimeWork(OneTimeWorkRequest oneTimeWorkRequest) {
        this.oneTimeWorkRequest = oneTimeWorkRequest;
    }

    private void test() {
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
        WorkManager workManager = WorkManager.getInstance(context);
        LiveData<WorkInfo> status = workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId());
        status.observe(Manager.getMainApplication(), new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo.getState().equals(WorkInfo.State.ENQUEUED)) {
                    // TODO :  Save this task, for auto start later

                } else if (workInfo.getState().isFinished()) {
                    // TODO : Remove from SharedPrefs.
                }
            }
        });
        workManager.enqueue(oneTimeWorkRequest);
    }

    private void saveWorkDetailsInSharedPrefs(Context context) {

    }
}
