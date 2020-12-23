package org.odk.collect.utilities.async

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.odk.collect.utilities.async.TaskSpec

abstract class WorkerAdapter(private val spec: TaskSpec, context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val completed = spec.getTask(applicationContext).get()

        return if (completed) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
