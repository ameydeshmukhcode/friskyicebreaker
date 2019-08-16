package com.frisky.icebreaker.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationFactory {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel createNotificationChannel(String channelID, String channelName,
                                                                String description, int importance) {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
        channel.setDescription(description);
        return channel;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel createNotificationChannel(String channelID, String channelName,
                                                                String description, int importance, Uri sound, AudioAttributes audio) {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
        channel.setDescription(description);
        channel.setSound(sound, audio);
        return channel;
    }

    public static Notification createNotification(Context context, String channelID, int icon, String title,
                                                  String content, int priority, PendingIntent intent) {
        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(context, channelID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(priority)
                .setContentIntent(intent);

        return builder.build();
    }

    public static Notification createNotification(Context context, String channelID, int icon, String title,
                                                  String content, int priority, PendingIntent intent, boolean autoCancel) {
        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(context, channelID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(priority)
                .setContentIntent(intent)
                .setAutoCancel(autoCancel);

        return builder.build();
    }
}
