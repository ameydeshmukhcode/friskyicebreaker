package com.frisky.icebreaker.orders;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.frisky.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
                    disableSession();
                }

                Log.d(getString(R.string.tag_debug), "Current data: " + snapshot.getData());
            }
            else {
                Log.d(getString(R.string.tag_debug), "Current data: null");
            }
        });
    }

    private void disableSession() {
        sharedPreferences.edit()
                .putBoolean("session_active", false)
                .remove("restaurant")
                .remove("restaurant_name")
                .remove("current_session")
                .remove("table_serial")
                .apply();

        stopSelf();
    }
}
