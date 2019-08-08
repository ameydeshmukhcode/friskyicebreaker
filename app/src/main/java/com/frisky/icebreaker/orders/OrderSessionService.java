package com.frisky.icebreaker.orders;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.frisky.icebreaker.HomeActivity;
import com.frisky.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class OrderSessionService extends Service {

    SharedPreferences sharedPreferences;
    NotificationManager notificationManager;

    public OrderSessionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(getString(R.string.tag_debug), "Service Created");

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        addListenerForOrderUpdates();
        addListenerForSessionEnd();

        notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(getString(R.string.n_channel_session)) == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel sessionChannel = new NotificationChannel(getString(R.string.n_channel_session),
                        getString(R.string.n_channel_name_session), importance);
                sessionChannel.setDescription(getString(R.string.n_channel_desc_session));
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(sessionChannel);

                NotificationChannel orderChannel = new NotificationChannel(getString(R.string.n_channel_order),
                        getString(R.string.n_channel_name_order), importance);
                orderChannel.setSound(null, null);
                orderChannel.setDescription(getString(R.string.n_channel_desc_order));

                notificationManager.createNotificationChannel(orderChannel);
            }
        }

        Intent notificationIntent = new Intent(this, MenuActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this, getString(R.string.n_channel_session))
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Welcome to " + sharedPreferences.getString("restaurant_name", ""))
                .setContentText("You're on " + sharedPreferences.getString("table_name", ""))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent);

        startForeground(R.integer.n_order_session_service, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(getString(R.string.tag_debug), "Service Started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getString(R.string.tag_debug), "Service Stopped");

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this, getString(R.string.n_channel_session))
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Session Ended")
                .setContentText("Click here to view your order receipt")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(R.integer.n_order_session_service, builder.build());

        sendSessionEndBroadcast(getApplicationContext());

        disableSession();
    }

    private void addListenerForOrderUpdates() {
        String restaurant = "";
        String currentSession = "";
        if (sharedPreferences.contains("restaurant_id")) {
            restaurant = sharedPreferences.getString("restaurant_id", "");
        }
        if (sharedPreferences.contains("session_id")) {
            currentSession = sharedPreferences.getString("session_id", "");
        }

        assert restaurant != null;
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("restaurants")
                .document(restaurant);

        docRef.collection("orders")
                .whereEqualTo("session_id", currentSession)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        System.err.println("Listen failed: " + e);
                        return;
                    }

                    assert queryDocumentSnapshots != null;
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.MODIFIED) {
                            Intent notificationIntent = new Intent(this, OrderActivity.class);
                            notificationIntent.putExtra("order_ack", true);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent pendingIntent =
                                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

                            NotificationCompat.Builder builder = new
                                    NotificationCompat.Builder(this, getString(R.string.n_channel_order))
                                    .setSmallIcon(R.drawable.logo)
                                    .setContentTitle("Order Update")
                                    .setContentText("Click here to view")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    // Set the intent that will fire when the user taps the notification
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            notificationManager.notify(R.integer.n_order_session_service, builder.build());

                            sendOrderUpdateBroadcast(getApplicationContext());
                        }
                    }
                });
    }

    private void addListenerForSessionEnd() {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Failed", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                boolean sessionActive = false;
                if (snapshot.contains("session_active")) {
                    sessionActive = (boolean) snapshot.get("session_active");
                }

                if (!sessionActive) {
                    stopSelf();
                }

                Log.d(getString(R.string.tag_debug), "Current data: " + snapshot.getData());
            }
            else {
                Log.d(getString(R.string.tag_debug), "Current data: null");
            }
        });
    }

    private void sendSessionEndBroadcast(Context context) {
        Intent intent = new Intent("SessionEnd");
        // You can also include some extra data.
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void sendOrderUpdateBroadcast(Context context) {
        Intent intent = new Intent("OrderUpdate");
        // You can also include some extra data.
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void disableSession() {
        sharedPreferences.edit()
                .putBoolean("session_active", false)
                .remove("restaurant_id")
                .remove("restaurant_name")
                .remove("session_id")
                .remove("table_name")
                .remove("table_id")
                .remove("bill_requested")
                .remove("bill_amount")
                .apply();
    }
}
