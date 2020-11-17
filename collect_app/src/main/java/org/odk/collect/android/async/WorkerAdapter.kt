package org.odk.collect.android.async

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.odk.collect.android.async.TaskSpec

abstract class WorkerAdapter(private val spec: TaskSpec, context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val completed = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spec.getTask(applicationContext).get()
        } else {
//            spec.getTask(applicationContext).get()
true       }

        return if (completed) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
