package com.samagra.parent.ui.HomeScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.samagra.commons.notifications.AppNotificationUtils;
import com.samagra.commons.notifications.NotificationRenderingActivity;

import org.odk.collect.android.activities.NotificationActivity;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction() != null && context != null) {
//            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
//                // Set the alarm here.
//                LocalData localData = new LocalData(context);
//                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min());
//                return;
//            }
//        }

        Log.d(TAG, "ovfgfgvnReceive: ");

        //Trigger the notification
        AppNotificationUtils.showNotification(context, NotificationActivity.class,
                "You have 5 unwatched videos", "Watch them now?");

    }
}