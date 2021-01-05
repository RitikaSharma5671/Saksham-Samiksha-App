package com.samagra.parent.ui.HomeScreen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.samagra.parent.R

class BackgroundTaskJava (val context : Context, params : WorkerParameters)
    : Worker(context, params){
    override fun doWork(): Result {

        Log.d("oneTimeWorkRequest","Uploading photos in background")

        sendNotification("Background Task","Succcessfully done")

        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
