package com.frisky.icebreaker.orders;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

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

    @SuppressWarnings("unchecked")
    private void addListenerForOrderUpdates() {
        String restaurant = "";
        String currentSession = "";
        if (sharedPreferences.contains("restaurant")) {
            restaurant = sharedPreferences.getString("restaurant", "");
        }
        if (sharedPreferences.contains("current_session")) {
            currentSession = sharedPreferences.getString("current_session", "");
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
                                Log.d("Added", "to Orders");
                                break;

                            case MODIFIED:
                                Map<String, Object> data;
                                data = (Map<String, Object>) dc.getDocument().get("items");
                                assert data != null;
                                for (Map.Entry<String, Object> entry : data.entrySet()) {
                                    String value = entry.getValue().toString();
                                    String itemID = entry.getKey();
                                    Log.d("Item", itemID);

                                    for (Map.Entry<OrderItem, OrderStatus> orderListEntry:
                                            mOrderList.entrySet()) {
                                        if (orderListEntry.getKey().getId().equals(itemID)) {
                                            if (value.contains("status=pending")) {
                                                orderListEntry.setValue(OrderStatus.PENDING);
                                                Log.d("Status", "Pending");
                                            }
                                            else if (value.contains("status=accepted")) {
                                                orderListEntry.setValue(OrderStatus.ACCEPTED);
                                                Log.d("Status", "Accepted");
                                            }
                                            else if (value.contains("status=rejected")) {
                                                orderListEntry.setValue(OrderStatus.REJECTED);
                                                Log.d("Status", "Rejected");
                                            }
                                            else if (value.contains("status=cancelled")) {
                                                orderListEntry.setValue(OrderStatus.CANCELLED);
                                                Log.d("Status", "Cancelled");
                                            }
                                        }
                                    }
                                }

                                orderListAdapter = new OrderListAdapter(getApplicationContext(), mOrderList);
                                mRecyclerOrderListView.setAdapter(orderListAdapter);
                                break;

                            case REMOVED:
                                Log.d("Removed", "from Orders");
                                break;

                            default:
                                break;
                        }
                    }
                });
    }
}
