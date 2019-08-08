package com.frisky.icebreaker.orders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.HomeActivity;
import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.frisky.icebreaker.ui.components.dialogs.ClearBillDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity implements ClearBillDialog.OnClearBillListener {

    ImageButton mBackButton;
    Button mClearBill;
    TextView mOrderTotalText;

    SharedPreferences sharedPreferences;

    OrderListAdapter orderListAdapter;
    RecyclerView mRecyclerOrderListView;

    ArrayList<OrderItem> mOrderList = new ArrayList<>();

    String restaurantID;
    String sessionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> super.onBackPressed());

        mClearBill = findViewById(R.id.button_clear_bill);
        mClearBill.setOnClickListener(v -> clearBill());

        mOrderTotalText = findViewById(R.id.text_order_total);

        TextView mTableSerial = findViewById(R.id.text_table);
        mTableSerial.setText(sharedPreferences.getString("table_name", ""));

        if (sharedPreferences.contains("restaurant_id")) {
            restaurantID = sharedPreferences.getString("restaurant_id", "");
        }
        if (sharedPreferences.contains("session_id")) {
            sessionID = sharedPreferences.getString("session_id", "");
        }

        mRecyclerOrderListView = findViewById(R.id.recycler_view_order_list);
        mRecyclerOrderListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerView.LayoutManager mOrderListViewLayoutManager;
        mOrderListViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerOrderListView.setLayoutManager(mOrderListViewLayoutManager);

        addListenerForOrderDetailsUpdate();

        if (getIntent().hasExtra("order_ack")) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            Intent notificationIntent = new Intent(this, MenuActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            String title = sharedPreferences.getString("restaurant_name", "") + " " +
                    sharedPreferences.getString("table_name", "");

            NotificationCompat.Builder builder = new
                    NotificationCompat.Builder(this, getString(R.string.n_channel_order))
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText("Click here to order more")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent);

            notificationManager.notify(R.integer.n_order_session_service, builder.build());
        }

        getOrderDetails();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        addListenerForOrderUpdates();
                    }
                },
                new IntentFilter("OrderUpdate"));
    }

    @SuppressWarnings("unchecked")
    private void getOrderDetails() {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("restaurants")
                .document(restaurantID);

        docRef.collection("orders")
                .whereEqualTo("session_id", sessionID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot document = task.getResult();
                        assert document != null;
                        for (DocumentSnapshot snapshot : document.getDocuments()) {

                            Map<String, Object> orderData = (Map<String, Object>) snapshot.get("items");
                            assert orderData != null;

                            for (Map.Entry<String, Object> entry : orderData.entrySet()) {
                                String itemID = entry.getKey();
                                HashMap<String, Object> item = (HashMap<String, Object>) entry.getValue();

                                String name = String.valueOf(item.get("name"));
                                int count = Integer.parseInt(String.valueOf(item.get("quantity")));
                                int price = Integer.parseInt(String.valueOf(item.get("cost")));
                                OrderItem orderItem = new OrderItem(itemID, name, count, (count * price));

                                if (String.valueOf(item.get("status")).equals("pending")) {
                                    orderItem.setStatus(OrderStatus.PENDING);
                                }
                                else if (String.valueOf(item.get("status")).equals("accepted")) {
                                    orderItem.setStatus(OrderStatus.ACCEPTED);
                                }
                                else if (String.valueOf(item.get("status")).equals("rejected")) {
                                    orderItem.setStatus(OrderStatus.REJECTED);
                                }
                                else if (String.valueOf(item.get("status")).equals("cancelled")) {
                                    orderItem.setStatus(OrderStatus.CANCELLED);
                                }
                                mOrderList.add(orderItem);
                            }
                        }

                        orderListAdapter = new OrderListAdapter(getApplicationContext(), mOrderList);
                        mRecyclerOrderListView.setAdapter(orderListAdapter);
                    }
                });
    }

    private void clearBill() {
        ClearBillDialog clearBillDialog = new ClearBillDialog();
        clearBillDialog.show(getSupportFragmentManager(), "clear bill dialog");
    }

    @SuppressWarnings("unchecked")
    private void addListenerForOrderUpdates() {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("restaurants")
                .document(restaurantID);

        docRef.collection("orders")
                .whereEqualTo("session_id", sessionID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("Error", "Listen failed.", e);
                        return;
                    }

                    int startIndex = 0;
                    int endIndex = 0;

                    assert value != null;

                    for (QueryDocumentSnapshot doc : value) {
                        Map<String, Object> orderData = (Map<String, Object>) doc.get("items");
                        assert orderData != null;

                        endIndex += orderData.size();

                        for (Map.Entry<String, Object> entry : orderData.entrySet()) {
                            String itemID = entry.getKey();
                            HashMap<String, Object> item = (HashMap<String, Object>) entry.getValue();

                            for (int i = startIndex; i < endIndex; i++) {

                                Log.d(getString(R.string.tag_debug), "Item ID " + itemID);
                                Log.d(getString(R.string.tag_debug), "Current ID " + mOrderList.get(i).getId());

                                if (mOrderList.get(i).getId().equals(itemID)) {
                                    if (String.valueOf(item.get("status")).equals("pending")) {
                                        mOrderList.get(i).setStatus(OrderStatus.PENDING);
                                    }
                                    else if (String.valueOf(item.get("status")).equals("accepted")) {
                                        mOrderList.get(i).setStatus(OrderStatus.ACCEPTED);
                                    }
                                    else if (String.valueOf(item.get("status")).equals("rejected")) {
                                        mOrderList.get(i).setStatus(OrderStatus.REJECTED);
                                    }
                                    else if (String.valueOf(item.get("status")).equals("cancelled")) {
                                        mOrderList.get(i).setStatus(OrderStatus.CANCELLED);
                                    }
                                    break;
                                }
                            }
                        }

                        startIndex = endIndex;
                    }

                    orderListAdapter.notifyDataSetChanged();
                });
    }

    private void addListenerForOrderDetailsUpdate() {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("restaurants")
                .document(Objects.requireNonNull(sharedPreferences.getString("restaurant_id", "")))
                .collection("sessions")
                .document(Objects.requireNonNull(sharedPreferences.getString("session_id", "")));

        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Failed", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                if (snapshot.contains("bill_amount")) {
                    mOrderTotalText.setText(Objects.requireNonNull(snapshot.get("bill_amount")).toString());
                    sharedPreferences.edit().putInt("bill_amount",
                            Integer.parseInt(Objects.requireNonNull(snapshot.get("bill_amount")).toString())).apply();
                }

                Log.d(getString(R.string.tag_debug), "Current data: " + snapshot.getData());
            }
            else {
                Log.d(getString(R.string.tag_debug), "Current data: null");
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
                        Map<String, Object> clearTable = new HashMap<>();
                        clearTable.put("bill_requested", true);

                        assert tableID != null;
                        firebaseFirestore.collection("restaurants")
                                .document(restaurantID)
                                .collection("tables")
                                .document(tableID)
                                .set(clearTable, SetOptions.merge())
                                .addOnSuccessListener(doc -> {
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);

                                    Intent notificationIntent = new Intent(this, HomeActivity.class);
                                    PendingIntent pendingIntent =
                                            PendingIntent.getActivity(this, 0, notificationIntent, 0);

                                    String billAmountString = "You've requested for the bill. Bill Amount: "
                                            + getString(R.string.rupee)
                                            + sharedPreferences.getInt("bill_amount", 0);

                                    NotificationCompat.Builder builder = new
                                            NotificationCompat.Builder(this, getString(R.string.n_channel_session))
                                            .setSmallIcon(R.drawable.logo)
                                            .setContentTitle("Bill Requested")
                                            .setContentText(billAmountString)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            // Set the intent that will fire when the user taps the notification
                                            .setContentIntent(pendingIntent);

                                    notificationManager.notify(R.integer.n_order_session_service, builder.build());

                                    sharedPreferences.edit().putBoolean("bill_requested", true).apply();

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
