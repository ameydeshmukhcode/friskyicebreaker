package com.frisky.icebreaker.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.activities.OrderActivity;
import com.frisky.icebreaker.adapters.OrderListAdapter;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class DineFragment extends Fragment {

    private RecyclerView mOrderListRecyclerView;
    private TextView recommendedItems;
    private MaterialCardView recommendedCard;

    private ArrayList<Object> mOrderList = new ArrayList<>();
    private OrderListAdapter orderListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity())
                .getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        if (sharedPreferences.getBoolean("session_active", false)) {
            if (sharedPreferences.getBoolean("bill_requested", false)) {
                view = inflater.inflate(R.layout.fragment_dine_bill_requested, container, false);
            } else {
                view = inflater.inflate(R.layout.fragment_dine_session_active, container, false);

                String restID = sharedPreferences.getString("restaurant_id", "");
                String sessionID = sharedPreferences.getString("session_id", "");

                TextView noOrders = view.findViewById(R.id.no_orders);

                TextView restaurant = view.findViewById(R.id.text_restaurant_name);
                TextView table = view.findViewById(R.id.text_table);
                restaurant.setText(sharedPreferences.getString("restaurant_name", ""));
                table.setText(sharedPreferences.getString("table_name", ""));

                Button showOrders = view.findViewById(R.id.button_show_orders);
                showOrders.setVisibility(View.GONE);

                recommendedCard = view.findViewById(R.id.card_recommendations);

                recommendedCard.setVisibility(View.GONE);

                recommendedItems = view.findViewById(R.id.text_recommendations);

                mOrderListRecyclerView = view.findViewById(R.id.recycler_view_last_order);

                RecyclerView.LayoutManager mOrderListViewLayoutManager;
                mOrderListViewLayoutManager = new LinearLayoutManager(getContext());

                mOrderListRecyclerView.setLayoutManager(mOrderListViewLayoutManager);
                mOrderListRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

                orderListAdapter = new OrderListAdapter(getContext(), mOrderList);
                mOrderListRecyclerView.setAdapter(orderListAdapter);

                getRecommendedItems(sharedPreferences.getString("restaurant_id", ""));

                if (sharedPreferences.contains("order_active")) {
                    addListenerForOrderUpdates(restID, sessionID);
                    noOrders.setVisibility(View.GONE);
                    showOrders.setVisibility(View.VISIBLE);
                }

                showOrders.setOnClickListener(v -> {
                    if (!sharedPreferences.contains("order_active")) {
                        Toast toast = Toast.makeText(getActivity(), "No orders placed yet!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        startActivity(new Intent(getActivity(), OrderActivity.class));
                    }
                });
            }
            return view;
        } else {
            view = inflater.inflate(R.layout.fragment_dine_scan_qr, container, false);
            return view;
        }
    }

    private void getRecommendedItems(String restaurantID) {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("restaurants")
                .document(restaurantID);

        docRef.collection("items")
                .whereEqualTo("recommended", true)
                .orderBy("name")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    assert queryDocumentSnapshots != null;
                    if (queryDocumentSnapshots.size() > 0) {
                        recommendedItems.setText("");
                        recommendedCard.setVisibility(View.VISIBLE);
                        StringBuilder currentItems = new StringBuilder(recommendedItems.getText().toString());
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.contains("name")) {
                                currentItems.append(snapshot.getString("name")).append(", ");
                            }
                        }
                        recommendedItems.setText(currentItems.substring(0, currentItems.length() - 2));
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void addListenerForOrderUpdates(String restaurantID, String sessionID) {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("restaurants")
                .document(restaurantID);

        docRef.collection("orders")
                .whereEqualTo("session_id", sessionID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener((queryDocumentSnapshots, e)-> {
                        mOrderList.clear();

                        assert queryDocumentSnapshots != null;

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                            Map<String, Object> orderData = (Map<String, Object>) snapshot.get("items");
                            assert orderData != null;

                            for (Map.Entry<String, Object> entry : orderData.entrySet()) {
                                String itemID = entry.getKey();
                                HashMap<String, Object> item = (HashMap<String, Object>) entry.getValue();

                                int count = Integer.parseInt(String.valueOf(item.get("quantity")));
                                String name = String.valueOf(item.get("name"));
                                int price = Integer.parseInt(String.valueOf(item.get("cost")));
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
                        }

                        orderListAdapter = new OrderListAdapter(getContext(), mOrderList);
                        mOrderListRecyclerView.setAdapter(orderListAdapter);
                });
    }
}
