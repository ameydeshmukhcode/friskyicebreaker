package com.frisky.icebreaker.activities;

import android.annotation.SuppressLint;
import android.app.Notification;
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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.OrderListAdapter;
import com.frisky.icebreaker.core.structures.OrderDetailsHeader;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.frisky.icebreaker.ui.components.dialogs.ClearBillDialog;
import com.frisky.icebreaker.ui.components.dialogs.ProgressDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.frisky.icebreaker.notifications.NotificationFactory.createNotification;

public class OrderActivity extends AppCompatActivity implements ClearBillDialog.OnClearBillListener {

    Button mBackButton;
    Button mClearBill;
    Button mAddMoreButton;
    TextView mOrderTotalText;
    TextView mGST;
    TextView mBillAmount;

    SharedPreferences sharedPreferences;

    OrderListAdapter orderListAdapter;
    RecyclerView mRecyclerOrderListView;

    ArrayList<Object> mOrderList = new ArrayList<>();

    String restaurantID;
    String sessionID;

    ProgressDialog progressDialog = new ProgressDialog("Requesting the Bill");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> onBackPressed());

        mAddMoreButton = findViewById(R.id.button_order_more);
        mAddMoreButton.setOnClickListener(v -> {
            onBackPressed();
        });

        mClearBill = findViewById(R.id.button_clear_bill);
        mClearBill.setOnClickListener(v -> clearBill());

        mOrderTotalText = findViewById(R.id.text_order_total);
        mBillAmount = findViewById(R.id.text_bill_amount);
        mGST = findViewById(R.id.text_gst);

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

        getOrderDetails();

        if (getIntent().hasExtra("order_ack")) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            Intent notificationIntent = new Intent(this, MenuActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            String title = sharedPreferences.getString("restaurant_name", "") + " " +
                    sharedPreferences.getString("table_name", "");

            Notification notification = createNotification(this, getString(R.string.n_channel_order), R.drawable.logo,
                    title, "Click here to order more",
                    NotificationCompat.PRIORITY_HIGH, pendingIntent);

            notificationManager.notify(R.integer.n_order_session_service, notification);
        }

        addListenerForOrderDetailsUpdate();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Intent clearBill = new Intent(getApplicationContext(), HomeActivity.class);
                        clearBill.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(clearBill);
                        finish();
                    }
                },
                new IntentFilter("SessionEnd"));

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        getOrderDetails();
                    }
                },
                new IntentFilter("OrderUpdate"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goToMenu = new Intent(this, MenuActivity.class);
        goToMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goToMenu);
        finish();
    }

    @SuppressWarnings("unchecked")
    private void getOrderDetails() {
        mOrderList.clear();

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

                        int docRank = document.size();

                        for (DocumentSnapshot snapshot : document.getDocuments()) {

                            Map<String, Object> orderData = (Map<String, Object>) snapshot.get("items");
                            assert orderData != null;

                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                            String orderTime = formatter.format((Objects
                                    .requireNonNull(snapshot.getTimestamp("timestamp")).toDate()));

                            OrderDetailsHeader orderDetailsHeader = new OrderDetailsHeader(orderTime, docRank);
                            mOrderList.add(orderDetailsHeader);

                            Log.d(getString(R.string.tag_debug), "Time " + orderDetailsHeader.getTime());
                            Log.d(getString(R.string.tag_debug),  "#" + orderDetailsHeader.getRank());

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
                            docRank--;
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
                if (snapshot.contains("amount_payable")) {
                    float amountPayable = Float.parseFloat(snapshot.getString("amount_payable"));
                    @SuppressLint("DefaultLocale")
                    String amountText = String.format("%.2f", amountPayable);
                    mOrderTotalText.setText(amountText);
                    sharedPreferences.edit().putString("amount_payable", snapshot.getString("amount_payable")).apply();
                }
                if (snapshot.contains("bill_amount")) {
                    float billAmount = Float.parseFloat(snapshot.getString("bill_amount"));
                    @SuppressLint("DefaultLocale")
                    String amountText = String.format("%.2f", billAmount);
                    mBillAmount.setText(amountText);
                }
                if (snapshot.contains("gst")) {
                    float gst = Float.parseFloat(snapshot.getString("gst"));
                    @SuppressLint("DefaultLocale")
                    String amountText = String.format("%.2f", gst);
                    mGST.setText(amountText);
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
            progressDialog.show(getSupportFragmentManager(), "progress dialog");
            progressDialog.setCancelable(false);
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

                                    float amountPayable = Float.parseFloat(sharedPreferences
                                            .getString("amount_payable", "0.00"));

                                    @SuppressLint("DefaultLocale")
                                    String amount = String.format("%.2f", amountPayable);

                                    String billAmountString = "You've requested for the bill. Bill Amount: "
                                            + getString(R.string.rupee)
                                            + amount;

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

                                    progressDialog.dismiss();
                                    Intent clearBill = new Intent(this, HomeActivity.class);
                                    clearBill.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(clearBill);
                                    finish();
                                })
                        .addOnFailureListener(e -> progressDialog.dismiss());
                    })
            .addOnFailureListener(e -> progressDialog.dismiss());
        }
    }
}
