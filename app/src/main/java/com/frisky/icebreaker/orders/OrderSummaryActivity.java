package com.frisky.icebreaker.orders;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.frisky.icebreaker.ui.base.UIActivity;
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
    TextView mTableName;
    TextView mOrderDateTime;
    TextView mFinalTotal;

    RecyclerView mOrderListRecyclerView;

    ArrayList<Object> mOrderList = new ArrayList<>();

    OrderListAdapter orderListAdapter;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        initUI();
    }

    @Override
    public void initUI() {
        mRestaurantName = findViewById(R.id.text_restaurant);
        mTableName = findViewById(R.id.text_table);
        mOrderDateTime = findViewById(R.id.text_date_time);
        mFinalTotal = findViewById(R.id.text_final_total);
        mOrderListRecyclerView = findViewById(R.id.recycler_view_final_order);

        RecyclerView.LayoutManager mOrderListViewLayoutManager;
        mOrderListViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        mOrderListRecyclerView.setLayoutManager(mOrderListViewLayoutManager);

        orderListAdapter = new OrderListAdapter(getApplicationContext(), mOrderList);
        mOrderListRecyclerView.setAdapter(orderListAdapter);

        if (sharedPreferences.contains("restaurant_name")) {
            mRestaurantName.setText(sharedPreferences.getString("restaurant_name", ""));
        }

        if (sharedPreferences.contains("table_name")) {
            mTableName.setText(sharedPreferences.getString("table_name", ""));
        }

        if (sharedPreferences.contains("session_id") && sharedPreferences.contains("restaurant_id")) {
            getOrderDetails(sharedPreferences.getString("restaurant_id", ""),
                    sharedPreferences.getString("session_id", ""));
        }

        sharedPreferences.edit()
                .remove("restaurant_name")
                .remove("table_name")
                .remove("restaurant_id")
                .remove("session_id")
                .apply();
    }

    private void getOrderSummary(String restaurant, String session) {
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurant)
                .collection("sessions").document(session)
                .get().addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       DocumentSnapshot snapshot = task.getResult();
                       if (snapshot == null)
                           return;
                       if (snapshot.contains("end_time")) {
                           @SuppressLint("SimpleDateFormat")
                           SimpleDateFormat formatter = new SimpleDateFormat("dd MMM YYYY hh:mm a");
                           String endTime = formatter.format((Objects
                                   .requireNonNull(snapshot.getTimestamp("end_time")).toDate()));
                           mOrderDateTime.setText(endTime);
                       }
                       if (snapshot.contains("bill_amount")) {
                           mFinalTotal.setText(String.valueOf(snapshot.get("bill_amount")));
                       }
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
                        mOrderListRecyclerView.setAdapter(orderListAdapter);

                        getOrderSummary(restaurantID, sessionID);
                    }
                });
    }
}
