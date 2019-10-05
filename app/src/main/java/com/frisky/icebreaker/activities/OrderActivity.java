package com.frisky.icebreaker.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.OrderListAdapter;
import com.frisky.icebreaker.core.structures.OrderHeader;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.frisky.icebreaker.ui.components.dialogs.ClearBillDialog;
import com.frisky.icebreaker.ui.components.dialogs.ProgressDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.functions.FirebaseFunctions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity implements UIActivity, ClearBillDialog.OnClearBillListener {

    SharedPreferences sharedPreferences;

    Button mBackButton;
    Button mClearBill;
    Button mAddMoreButton;
    TextView mOrderTotalText;
    TextView mGST;
    TextView mBillAmount;

    OrderListAdapter orderListAdapter;
    RecyclerView mRecyclerOrderListView;

    String restaurantID;
    String sessionID;
    ArrayList<Object> mOrderList = new ArrayList<>();

    ProgressDialog progressDialog = new ProgressDialog("Requesting the Bill");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        initUI();

        addListenerForOrderUpdates();
        addListenerForOrderDetailsUpdate();
    }

    @Override
    public void initUI() {
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> onBackPressed());

        mAddMoreButton = findViewById(R.id.button_order_more);
        mAddMoreButton.setOnClickListener(v -> onBackPressed());

        mClearBill = findViewById(R.id.button_clear_bill);
        mClearBill.setOnClickListener(v -> {
            ClearBillDialog clearBillDialog = new ClearBillDialog();
            clearBillDialog.show(getSupportFragmentManager(), "clear bill dialog");
        });

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goToMenu = new Intent(this, MenuActivity.class);
        goToMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goToMenu);
        finish();
    }

    @Override
    public void clearBill(boolean choice) {
        if (choice) {
            progressDialog.show(getSupportFragmentManager(), "progress dialog");
            progressDialog.setCancelable(false);

            String restaurantID = sharedPreferences.getString("restaurant_id", "");
            String sessionID = sharedPreferences.getString("session_id", "");
            String tableID = sharedPreferences.getString("table_id", "");

            Map<String, Object> data = new HashMap<>();
            data.put("restaurant", restaurantID);
            data.put("table", tableID);
            data.put("session", sessionID);

            FirebaseFunctions.getInstance()
                    .getHttpsCallable("requestBill")
                    .call(data)
                    .continueWith(task -> {
                        if (task.isSuccessful()) {
                            navigateBackHome();
                        } else if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "Something went wrong :( Try again.", Toast.LENGTH_SHORT).show();
                        }

                        return "requestBill";
                    });
        }
    }

    @SuppressWarnings("unchecked")
    private void addListenerForOrderUpdates() {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("restaurants")
                .document(restaurantID);

        docRef.collection("orders")
                .whereEqualTo("session_id", sessionID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(((queryDocumentSnapshots, e) -> {
                    assert queryDocumentSnapshots != null;

                    mOrderList.clear();

                    int docRank = queryDocumentSnapshots.size();

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                        Map<String, Object> orderData = (Map<String, Object>) snapshot.get("items");
                        assert orderData != null;

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                        String orderTime = formatter.format((Objects
                                .requireNonNull(snapshot.getTimestamp("timestamp")).toDate()));

                        OrderHeader orderHeader = new OrderHeader(orderTime, docRank);
                        mOrderList.add(orderHeader);

                        for (Map.Entry<String, Object> entry : orderData.entrySet()) {
                            String itemID = entry.getKey();
                            HashMap<String, Object> item = (HashMap<String, Object>) entry.getValue();

                            int count = Integer.parseInt(String.valueOf(item.get("quantity")));
                            int price = Integer.parseInt(String.valueOf(item.get("cost")));
                            String name = String.valueOf(item.get("name"));
                            OrderItem orderItem = new OrderItem(itemID, name, count, (count * price));

                            if (String.valueOf(item.get("status")).equals("pending")) {
                                orderItem.setStatus(OrderStatus.PENDING);
                            } else if (String.valueOf(item.get("status")).equals("accepted")) {
                                orderItem.setStatus(OrderStatus.ACCEPTED);
                            } else if (String.valueOf(item.get("status")).equals("rejected")) {
                                orderItem.setStatus(OrderStatus.REJECTED);
                            } else if (String.valueOf(item.get("status")).equals("cancelled")) {
                                orderItem.setStatus(OrderStatus.CANCELLED);
                            }
                            mOrderList.add(orderItem);
                        }
                        docRank--;
                    }

                    orderListAdapter = new OrderListAdapter(getApplicationContext(), mOrderList);
                    mRecyclerOrderListView.setAdapter(orderListAdapter);
                }));
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
            } else {
                Log.d(getString(R.string.tag_debug), "Current data: null");
            }
        });
    }

    private void navigateBackHome() {
        sharedPreferences.edit().putBoolean("bill_requested", true).apply();

        Intent clearBill = new Intent(this, HomeActivity.class);
        clearBill.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(clearBill);
        finish();
    }
}
