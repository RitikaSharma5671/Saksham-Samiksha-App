package com.samagra.parent.ui.HomeScreen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.samagra.parent.R
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import java.net.SocketException
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class ReminderWorker( appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val REMINDER_WORK_NAME = "reminder"
        private const val PARAM_NAME = "name" // optional - send parameter to worker
        // private const val RESULT_ID = "id"

        @RequiresApi(Build.VERSION_CODES.O)
        fun runAt() {
            val workManager = WorkManager.getInstance()

            // trigger at 8:30am
            val alarmTime = LocalTime.of(9, 44)
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            val nowTime = now.toLocalTime()
            // if same time, schedule for next day as well
            // if today's time had passed, schedule for next day
            if (nowTime == alarmTime || nowTime.isAfter(alarmTime)) {
                now = now.plusMinutes(5)
            }
            now = now.withHour(alarmTime.hour).withMinute(alarmTime.minute) // .withSecond(alarmTime.second).withNano(alarmTime.nano)
            val duration = Duration.between(LocalDateTime.now(), now)

            Timber.d("runAt=${duration.seconds}s")

            // optional constraints
            /*
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
             */

            // optional data
            val data = workDataOf(PARAM_NAME to "Timer 01")

            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(duration.seconds, TimeUnit.SECONDS)
                    // .setConstraints(constraints)
                    .setInputData(data) // optional
                    .build()

            workManager.enqueueUniqueWork(REMINDER_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
        }

        fun cancel() {
            Timber.d("cancel")
            val workManager = WorkManager.getInstance()
            workManager.cancelUniqueWork(REMINDER_WORK_NAME)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = coroutineScope {
        val worker = this@ReminderWorker
        val context = applicationContext

        val name = inputData.getString(PARAM_NAME)
        Timber.d("doWork=$name")

        var isScheduleNext = true
        try {
            sendNotification("Hello", "Namaste")

            Result.success()
        }
        catch (e: Exception) {
            // only retry 3 times
            if (runAttemptCount > 3) {
                Timber.d("runAttemptCount=$runAttemptCount, stop retry")
                return@coroutineScope Result.success()
            }

            // retry if network failure, else considered failed
            when(e.cause) {
                is SocketException -> {
                    Timber.e(e.toString(), e.message)
                    isScheduleNext = false
                    Result.retry()
                }
                else -> {
                    Timber.e(e)
                    Result.failure()
                }
            }
        }
        finally {
            // only schedule next day if not retry, else it will overwrite the retry attempt
            // - because we use uniqueName with ExistingWorkPolicy.REPLACE
            if (isScheduleNext) {
                runAt() // schedule for next day
            }
        }
    }




    private fun sendNotification(title: String, message: String) {
        val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //If on Oreo then notification required a notification channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                    NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(
                applicationContext,
                "default"
        )
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_saksham)
        notificationManager.notify(1, notification.build())
    }
}