package com.frisky.icebreaker.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.activities.HomeActivity;
import com.frisky.icebreaker.activities.VisitActivity;
import com.frisky.icebreaker.notifications.NotificationFactory;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class FriskyMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        assert notificationManager != null;

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> payloadData = remoteMessage.getData();

            if (payloadData.containsKey("end_session") && payloadData.get("end_session").matches("yes")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(
                            NotificationFactory.createNotificationChannel(getString(R.string.n_channel_session),
                                    getString(R.string.n_channel_name_session),
                                    getString(R.string.n_channel_desc_session),
                                    NotificationManager.IMPORTANCE_DEFAULT));
                }

                Intent notificationIntent = new Intent(this, VisitActivity.class);
                notificationIntent.putExtra("session_id",
                        sharedPreferences.getString("session_id", ""));
                notificationIntent.putExtra("restaurant_id",
                        sharedPreferences.getString("restaurant_id", ""));
                notificationIntent.putExtra("restaurant_name",
                        sharedPreferences.getString("restaurant_name", ""));

                sharedPreferences.edit()
                        .putBoolean("session_active", false)
                        .remove("session_id")
                        .remove("restaurant_id")
                        .remove("restaurant_name")
                        .remove("table_id")
                        .remove("table_name")
                        .remove("bill_requested")
                        .remove("bill_amount")
                        .remove("order_active")
                        .apply();

                Intent restartAtHome = new Intent(getApplicationContext(), HomeActivity.class);
                restartAtHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);

                int requestCode = new Random().nextInt();

                PendingIntent pendingIntent =
                        PendingIntent.getActivity(this, requestCode, notificationIntent, 0);

                Notification notification = NotificationFactory.createNotification(this,
                        getString(R.string.n_channel_session),
                        R.drawable.logo,
                        "Session Ended",
                        "Click here to view your order receipt",
                        NotificationCompat.PRIORITY_HIGH,
                        pendingIntent,
                        true);

                notificationManager.notify(1402, notification);

                startActivity(restartAtHome);
            }
        }
    }
}
