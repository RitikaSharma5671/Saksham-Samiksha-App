package com.samagra.commons.notifications;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.samagra.commons.R;

public class AppNotificationUtils {
    public static final int DAILY_REMINDER_REQUEST_CODE = 100;
    public static final String CHANNEL_ID = "collect_notification_channel";

    private AppNotificationUtils() {
    }

    public static void showNotification(Context context, Class<?> cls, String title, String content) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent notifyIntent = new Intent(context, cls);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.putExtra("title", title);
        notifyIntent.putExtra("message", content);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, DAILY_REMINDER_REQUEST_CODE,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        Notification notification = builder.setContentTitle(title)
                .setContentText(content).setAutoCancel(true)
                .setSound(alarmSound).setSmallIcon(R.drawable.saksham_notif_icon)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, notification);
    }

    public static void createNotificationChannel(Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = application.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(new NotificationChannel(
                        CHANNEL_ID,
                        application.getString(R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT)
                );
            }
        }
    }

    public static void showNotification(Context context, PendingIntent contentIntent,
                                        int notificationId,
                                        int title,
                                        String contentText) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID).setContentIntent(contentIntent);

        builder
                .setContentTitle(context.getString(title))
                .setContentText(contentText)
                .setSmallIcon(getNotificationAppIcon())
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }


    public static void updateFirebaseToken(@NonNull Context context, String baseApiUrl, String apiKey) {
        new PushMessagingService().setContext(context, baseApiUrl, apiKey).getCurrentToken(context);
    }

    public static void showNotification(Context activityContext, PendingIntent contentIntent,
                                        int notificationId,
                                        String title,
                                        String contentText) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activityContext, CHANNEL_ID).setContentIntent(contentIntent);

        builder
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(getNotificationAppIcon())
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) activityContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }

    private static int getNotificationAppIcon() {
        return R.drawable.saksham_notif_icon;
    }

}
