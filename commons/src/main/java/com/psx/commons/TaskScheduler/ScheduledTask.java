package com.psx.commons.TaskScheduler;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.UUID;

public interface ScheduledTask {

    @Nullable
    UUID getScheduledTaskId();

    void enqueueTask(Context context);

    void cancelTask(Context context);
}
