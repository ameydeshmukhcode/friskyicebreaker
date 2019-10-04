package com.frisky.icebreaker.services;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.activities.HomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FriskyService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);;

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> payloadData = remoteMessage.getData();

            if (payloadData.containsKey("end_session") && payloadData.get("end_session").matches("yes")) {
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
            }

            Intent restartAtHome = new Intent(getApplicationContext(), HomeActivity.class);
            restartAtHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(restartAtHome);
        }

        if (remoteMessage.getNotification() != null) {

        }
    }
}
