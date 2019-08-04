package com.frisky.icebreaker.orders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.HomeActivity;
import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.frisky.icebreaker.ui.components.dialogs.ClearBillDialog;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements ClearBillDialog.OnClearBillListener {

    ImageButton mBackButton;
    Button mClearBill;

    SharedPreferences sharedPreferences;

    OrderListAdapter orderListAdapter;
    RecyclerView mRecyclerOrderListView;

    HashMap<OrderItem, OrderStatus> mOrderList = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> super.onBackPressed());

        mClearBill = findViewById(R.id.button_clear_bill);
        mClearBill.setOnClickListener(v -> clearBill());

        TextView mTableSerial = findViewById(R.id.text_table);
        mTableSerial.setText(sharedPreferences.getString("table_name", ""));

        mRecyclerOrderListView = findViewById(R.id.recycler_view_order_list);

        RecyclerView.LayoutManager mOrderListViewLayoutManager;
        mOrderListViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerOrderListView.setLayoutManager(mOrderListViewLayoutManager);

        addListenerForOrderUpdates();

        if (getIntent().hasExtra("order_list")) {
            mOrderList = (HashMap<OrderItem, OrderStatus>) getIntent().getSerializableExtra("order_list");
        }

        orderListAdapter = new OrderListAdapter(getApplicationContext(), mOrderList);
        mRecyclerOrderListView.setAdapter(orderListAdapter);
    }

    private void clearBill() {
        ClearBillDialog clearBillDialog = new ClearBillDialog();
        clearBillDialog.show(getSupportFragmentManager(), "clear bill dialog");
    }

    @SuppressWarnings("unchecked")
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
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(getString(R.string.tag_debug), "Added to Orders");
                                break;

                            case MODIFIED:
                                Map<String, Object> data;
                                data = (Map<String, Object>) dc.getDocument().get("items");
                                assert data != null;
                                for (Map.Entry<String, Object> entry : data.entrySet()) {
                                    String value = entry.getValue().toString();
                                    String itemID = entry.getKey();
                                    Log.d(getString(R.string.tag_debug), "Item " + itemID);

                                    for (Map.Entry<OrderItem, OrderStatus> orderListEntry:
                                            mOrderList.entrySet()) {
                                        if (orderListEntry.getKey().getId().equals(itemID)) {
                                            if (value.contains("status=pending")) {
                                                orderListEntry.setValue(OrderStatus.PENDING);
                                                Log.d(getString(R.string.tag_debug), "Status Pending");
                                            }
                                            else if (value.contains("status=accepted")) {
                                                orderListEntry.setValue(OrderStatus.ACCEPTED);
                                                Log.d(getString(R.string.tag_debug), "Status Accepted");
                                            }
                                            else if (value.contains("status=rejected")) {
                                                orderListEntry.setValue(OrderStatus.REJECTED);
                                                Log.d(getString(R.string.tag_debug), "Status Rejected");
                                            }
                                            else if (value.contains("status=cancelled")) {
                                                orderListEntry.setValue(OrderStatus.CANCELLED);
                                                Log.d(getString(R.string.tag_debug), "Status Cancelled");
                                            }
                                        }
                                    }
                                }

                                orderListAdapter = new OrderListAdapter(getApplicationContext(), mOrderList);
                                mRecyclerOrderListView.setAdapter(orderListAdapter);
                                break;

                            case REMOVED:
                                Log.d(getString(R.string.tag_debug), "Removed from Orders");
                                break;

                            default:
                                break;
                        }
                    }
                });
    }

    @Override
    public void clearBill(boolean choice) {
        if (choice) {
            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            String restaurantID = sharedPreferences.getString("restaurant_id", "");
            String sessionID = sharedPreferences.getString("session_id", "");
            String tableID = sharedPreferences.getString("table_id", "");

            Map<String, Object> requestBill = new HashMap<>();
            requestBill.put("bill_requested", true);

            assert restaurantID != null;
            assert sessionID != null;
            firebaseFirestore.collection("restaurants")
                    .document(restaurantID)
                    .collection("sessions")
                    .document(sessionID)
                    .set(requestBill, SetOptions.merge())
                    .addOnSuccessListener(documentReference -> {
                        Map<String, Object> needsAttention = new HashMap<>();
                        requestBill.put("needs_attention", true);

                        assert tableID != null;
                        firebaseFirestore.collection("restaurants")
                                .document(restaurantID)
                                .collection("tables")
                                .document(tableID)
                                .set(needsAttention, SetOptions.merge())
                                .addOnSuccessListener(doc -> {
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);

                                    Intent notificationIntent = new Intent(this, HomeActivity.class);
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

                                    notificationManager.notify(1402, builder.build());

                                    Intent clearBill = new Intent(this, HomeActivity.class);
                                    clearBill.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(clearBill);
                                    finish();
                                });
                    });
        }
    }
}
