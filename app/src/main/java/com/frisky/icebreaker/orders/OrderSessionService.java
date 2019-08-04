package com.frisky.icebreaker.orders;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.frisky.icebreaker.HomeActivity;
import com.frisky.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class OrderSessionService extends Service {

    SharedPreferences sharedPreferences;

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
        addListenerForSessionEnd();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(getString(R.string.n_channel_orders)) == null) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(getString(R.string.n_channel_orders),
                        getString(R.string.n_channel_name_orders), importance);
                channel.setDescription(getString(R.string.n_channel_desc_orders));
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(this, MenuActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this, getString(R.string.n_channel_orders))
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Welcome to " + sharedPreferences.getString("restaurant_name", ""))
                .setContentText("You're on " + sharedPreferences.getString("table_name", ""))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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

        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this, getString(R.string.n_channel_orders))
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Bill Requested")
                .setContentText("Payment complete. Hope you had a great experience at " + sharedPreferences.getString("restaurant_name", ""))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent);

        notificationManager.notify(R.integer.n_session_end, builder.build());

        disableSession();
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
                    getSessionEndDetails();
                }

                Log.d(getString(R.string.tag_debug), "Current data: " + snapshot.getData());
            }
            else {
                Log.d(getString(R.string.tag_debug), "Current data: null");
            }
        });
    }

    private void getSessionEndDetails() {
        final String restaurantID = sharedPreferences.getString("restaurant_id", "");
        final String sessionID = sharedPreferences.getString("session_id", "");

        assert restaurantID != null;
        assert sessionID != null;
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("restaurants")
                .document(restaurantID).collection("sessions").document(sessionID);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null)
                    return;
                if (document.exists()) {
                    Log.d(getString(R.string.tag_debug), "DocumentSnapshot data: " + document.getData());
                    stopSelf();
                }
                else {
                    Log.e("Doesn't exist", "No such document");
                }
            }
            else {
                Log.e("Task", "failed with ", task.getException());
            }
        });
    }

    private void disableSession() {
        sharedPreferences.edit()
                .putBoolean("session_active", false)
                .remove("restaurant_id")
                .remove("restaurant_name")
                .remove("session_id")
                .remove("table_name")
                .remove("table_id")
                .apply();
    }
}
