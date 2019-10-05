package com.frisky.icebreaker.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.OrderListAdapter;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderSummaryActivity extends AppCompatActivity implements UIActivity {

    TextView mRestaurantName;
    TextView mOrderDateTime;
    TextView mFinalTotal;
    TextView mOrderTotal;
    TextView mGST;
    Button mBackButton;

    RecyclerView mOrderListRecyclerView;

    ArrayList<Object> mOrderList = new ArrayList<>();

    OrderListAdapter orderListAdapter;
    ProgressBar progressBar;

    ConstraintLayout mLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        initUI();
    }

    @Override
    public void initUI() {
        mRestaurantName = findViewById(R.id.text_restaurant);
        mOrderDateTime = findViewById(R.id.text_date_time);
        mFinalTotal = findViewById(R.id.text_final_total);
        mOrderTotal = findViewById(R.id.text_order_total);
        mGST = findViewById(R.id.text_gst);
        mOrderListRecyclerView = findViewById(R.id.recycler_view_final_order);

        mLayout = findViewById(R.id.layout_order_summary);
        mLayout.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progress_summary);

        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> this.onBackPressed());

        RecyclerView.LayoutManager mOrderListViewLayoutManager;
        mOrderListViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        mOrderListRecyclerView.setLayoutManager(mOrderListViewLayoutManager);
        mOrderListRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        orderListAdapter = new OrderListAdapter(getApplicationContext(), mOrderList);
        mOrderListRecyclerView.setAdapter(orderListAdapter);

        if (getIntent().hasExtra("session_id") && getIntent().hasExtra("restaurant_id")) {
            getOrderSummary(getIntent().getStringExtra("restaurant_id"),
                    getIntent().getStringExtra("session_id"));
        }

        if (getIntent().hasExtra("restaurant_name")) {
            mRestaurantName.setText(getIntent().getStringExtra("restaurant_name"));
        }
    }

    private void getOrderSummary(String restaurant, String session) {
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurant)
                .collection("sessions").document(session)
                .get().addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       DocumentSnapshot snapshot = task.getResult();
                       if (snapshot == null) return;
                       if (snapshot.contains("end_time")) {
                           @SuppressLint("SimpleDateFormat")
                           SimpleDateFormat formatter = new SimpleDateFormat("dd MMM YYYY hh:mm a");
                           String endTime = formatter.format((Objects
                                   .requireNonNull(snapshot.getTimestamp("end_time")).toDate()));
                           mOrderDateTime.setText(endTime);
                       }
                       if (snapshot.contains("amount_payable")) {
                           float amountPayable = Float.parseFloat(snapshot.getString("amount_payable"));

                           @SuppressLint("DefaultLocale")
                           String amount = String.format("%.2f", amountPayable);
                           mFinalTotal.setText(amount);
                       }
                       if (snapshot.contains("bill_amount")) {
                           float billAmount = Float.parseFloat(snapshot.getString("bill_amount"));

                           @SuppressLint("DefaultLocale")
                           String amount = String.format("%.2f", billAmount);
                           mOrderTotal.setText(amount);
                       }
                       if (snapshot.contains("gst")) {
                           float gst = Float.parseFloat(snapshot.getString("gst"));

                           @SuppressLint("DefaultLocale")
                           String amount = String.format("%.2f", gst);
                           mGST.setText(amount);
                       }
                       getOrderDetails(restaurant, session);
                   }
                });
    }

    @SuppressWarnings("unchecked")
    private void getOrderDetails(String restaurantID, String sessionID) {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("restaurants")
                .document(restaurantID);

        docRef.collection("orders")
                .whereEqualTo("session_id", sessionID)
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

                                if (orderItem.getStatus() != OrderStatus.PENDING) {
                                    mOrderList.add(orderItem);
                                }
                            }
                        }

                        orderListAdapter = new OrderListAdapter(getApplicationContext(), mOrderList);
                        mOrderListRecyclerView.setAdapter(orderListAdapter);
                        mLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
