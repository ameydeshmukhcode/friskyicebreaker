package com.frisky.icebreaker.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.core.structures.MutableInt;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.frisky.icebreaker.orders.OrderingAssistant.SESSION_ACTIVE;

public class MenuActivity extends AppCompatActivity implements UIActivity,
        MenuItemListAdapter.OnOrderListChangeListener {

    ImageButton mBackButton;
    TextView mRestName;
    TextView mTableSerial;

    private List<Object> menu = new ArrayList<>();
    private HashMap<String, String> categories = new HashMap<>();

    int orderAmount = 0;
    HashMap<MenuItem, MutableInt> orderList = new HashMap<>();

    RecyclerView.Adapter mMenuListViewAdapter;
    RecyclerView mRecyclerMenuListView;

    ConstraintLayout bottomSheetOrder;
    TextView orderAmountText;
    Button viewOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SESSION_ACTIVE || getIntent().hasExtra("start_new_session")) {
            setContentView(R.layout.activity_menu);
        }
        else {
            setContentView(R.layout.activity_menu_empty_state);
        }

        initUI();
    }

    @Override
    public void initUI() {
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> MenuActivity.super.onBackPressed());

        if (SESSION_ACTIVE) {
            mRestName = findViewById(R.id.text_pub_name);
            mTableSerial = findViewById(R.id.text_table);

            bottomSheetOrder = findViewById(R.id.bottom_sheet_order);
            bottomSheetOrder.setVisibility(View.GONE);

            orderAmountText = findViewById(R.id.text_order_amount);
            viewOrderButton = findViewById(R.id.button_view_order);
            viewOrderButton.setOnClickListener(v -> {
                Intent showOrder = new Intent(getApplicationContext(), OrderActivity.class);
                showOrder.putExtra("table_id", mTableSerial.getText().toString());
                showOrder.putExtra("order_list", orderList);
                startActivity(showOrder);
            });

            restoreUserSession();
        }
        else if (getIntent().hasExtra("start_new_session")) {
            if (getIntent().hasExtra("table_id")
                    && getIntent().hasExtra("restaurant_id")) {

                mRestName = findViewById(R.id.text_pub_name);
                mTableSerial = findViewById(R.id.text_table);

                final String restID = getIntent().getStringExtra("restaurant_id");
                final String tableID = getIntent().getStringExtra("table_id");

                getRestaurantAndTableDetails(restID, tableID);
                initUserSession(restID, tableID);
                setMenu(restID);
            }
        }
    }

    private void initUserSession(final String restID, final String tableID) {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("table_id", tableID);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            data.put("created_by", FirebaseAuth.getInstance().getCurrentUser().getUid());

        data.put("start_time", new Timestamp(System.currentTimeMillis()));
        data.put("is_active", true);

        firebaseFirestore.collection("restaurants")
                .document(restID)
                .collection("sessions")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    final String sessionID = documentReference.getId();
                    Log.e("", "DocumentSnapshot written with ID: " + documentReference.getId());

                    Map<String, Object> data1 = new HashMap<>();
                    data1.put("session_active", true);
                    data1.put("current_session", sessionID);
                    data1.put("restaurant", restID);

                    SESSION_ACTIVE = true;

                    firebaseFirestore.collection("users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .set(data1, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Map<String, Object> data11 = new HashMap<>();
                                data11.put("occupied", true);
                                data11.put("session_id", sessionID);

                                firebaseFirestore.collection("restaurants")
                                        .document(restID)
                                        .collection("tables")
                                        .document(tableID)
                                        .set(data11, SetOptions.merge())
                                        .addOnSuccessListener(aVoid1 -> Log.i("Success", "Table details updated"));
                            });
                })
                .addOnFailureListener(e -> Log.e("", "Error adding document", e));
    }

    private void restoreUserSession() {
        if (getIntent().hasExtra("table_number")
                && getIntent().hasExtra("restaurant_name")
                && getIntent().hasExtra("restaurant_id")) {
            mRestName.setText(getIntent().getStringExtra("restaurant_name"));
            mTableSerial.setText(getIntent().getStringExtra("table_number"));
            setMenu(getIntent().getStringExtra("restaurant_id"));
        }
    }

    private void getRestaurantAndTableDetails(String restID, String tableID) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference resRef = firebaseFirestore.collection("restaurants").document(restID);
        resRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null)
                    return;
                if (document.exists()) {
                    mRestName.setText(document.getString("name"));
                    Log.i("Exists", "DocumentSnapshot data: " + document.getData());
                }
                else {
                    Log.e("Doesn't exist", "No such document");
                }
            }
            else {
                Log.e("Task", "failed with ", task.getException());
            }
        });

        DocumentReference tableRef = firebaseFirestore
                .collection("restaurants")
                .document(restID)
                .collection("tables")
                .document(tableID);

        tableRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null)
                    return;
                if (document.exists()) {
                    String tableSerial = "Table " + document.get("number");
                    mTableSerial.setText(tableSerial);
                    Log.i("Exists", "DocumentSnapshot data: " + document.getData());
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

    private void setMenu(String restaurant) {
        mRecyclerMenuListView = findViewById(R.id.recycler_view);
        mRecyclerMenuListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerView.LayoutManager mMenuListViewLayoutManager;
        // use a linear layout manager
        mMenuListViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerMenuListView.setLayoutManager(mMenuListViewLayoutManager);

        // specify an adapter (see also next example)
        mMenuListViewAdapter = new MenuItemListAdapter(menu, categories, this);
        mRecyclerMenuListView.setAdapter(mMenuListViewAdapter);

        prepareMenuData(restaurant);
    }

    private void prepareMenuData(String restID) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        CollectionReference categoriesRef = firestore.collection("restaurants").document(restID)
                .collection("categories");

        categoriesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    for (DocumentSnapshot document : task.getResult()) {
                        categories.put(document.getId(), document.getString("name"));
                    }
                }
            }
        });

        Query itemsListRef = firestore.collection("restaurants").document(restID)
                .collection("items").orderBy("category_id");

        itemsListRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String category = "";
                if (task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String currentCategory = document.getString("category_id");
                        if (currentCategory == null)
                            return;
                        if (!category.equals(currentCategory)) {
                            category = currentCategory;
                            menu.add(currentCategory);
                        }
                        String name = document.getString("name");
                        int cost = Integer.parseInt(Objects.requireNonNull(document.getString("cost")));
                        MenuItem item = new MenuItem(document.getId(), name, name, cost);
                        menu.add(item);
                        mMenuListViewAdapter.notifyDataSetChanged();
                    }
                }
            }
            else {
                Log.e("error", "Error getting documents: ", task.getException());
            }
        });
    }

    @Override
    public void addToOrder(MenuItem item) {
        orderAmount += item.getPrice();
        bottomSheetOrder.setVisibility(View.VISIBLE);
        orderAmountText.setText(String.valueOf(orderAmount));

        if (orderList.size() == 0) {
            orderList.put(item, new MutableInt());
        }
        else {
            boolean updatedItem = false;
            for (Map.Entry<MenuItem, MutableInt> entry : orderList.entrySet()) {
                if (entry.getKey().getId().equals(item.getId())) {
                    MutableInt count = entry.getValue();
                    count.increment();
                    updatedItem = true;
                }
            }
            if (!updatedItem) {
                orderList.put(item, new MutableInt());
            }
        }

        mRecyclerMenuListView.setPadding(0, 0, 0, 0);
        mRecyclerMenuListView.setPadding(0, 0, 0, 225);
        mRecyclerMenuListView.setClipToPadding(false);
    }

    @Override
    public void removeFromOrder(MenuItem item) {
        orderAmount -= item.getPrice();

        for (Map.Entry<MenuItem, MutableInt> entry : orderList.entrySet()) {
            if (entry.getKey().getId().equals(item.getId())) {
                MutableInt count = entry.getValue();
                count.decrement();
                if (count.getValue() == 0) {
                    orderList.remove(entry.getKey());
                }
                break;
            }
        }

        if (orderAmount > 0) {
            orderAmountText.setText(String.valueOf(orderAmount));
        }
        else {
            bottomSheetOrder.setVisibility(View.GONE);
            mRecyclerMenuListView.setPadding(0, 0, 0, 0);
            mRecyclerMenuListView.setClipToPadding(false);
        }
    }
}
