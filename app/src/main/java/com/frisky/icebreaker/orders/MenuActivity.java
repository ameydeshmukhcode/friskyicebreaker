package com.frisky.icebreaker.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MenuActivity extends AppCompatActivity implements UIActivity,
        MenuItemListAdapter.OnOrderListChangeListener {

    SharedPreferences sharedPreferences;

    ImageButton mBackButton;
    TextView mRestName;
    TextView mTableSerial;

    private List<Object> mMenu = new ArrayList<>();
    private HashMap<String, String> mCategories = new HashMap<>();

    int mOrderTotal = 0;
    HashMap<MenuItem, MutableInt> mOrderList = new HashMap<>();

    RecyclerView.Adapter mMenuListViewAdapter;
    RecyclerView mRecyclerMenuListView;

    ConstraintLayout mBottomSheetOrder;
    TextView mOrderTotalText;
    Button mViewOrderButton;

    boolean isSessionActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        isSessionActive = getSharedPreferences(getString(R.string.app_name),
                MODE_PRIVATE).getBoolean("session_active", false);

        if (isSessionActive || getIntent().hasExtra("start_new_session")) {
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

        if (isSessionActive) {
            mRestName = findViewById(R.id.text_pub_name);
            mTableSerial = findViewById(R.id.text_table);

            mBottomSheetOrder = findViewById(R.id.bottom_sheet_order);
            mBottomSheetOrder.setVisibility(View.GONE);

            mOrderTotalText = findViewById(R.id.text_order_amount);
            mViewOrderButton = findViewById(R.id.button_view_order);
            mViewOrderButton.setOnClickListener(v -> {
                Intent showOrder = new Intent(getApplicationContext(), OrderActivity.class);
                showOrder.putExtra("table_id", mTableSerial.getText().toString());
                showOrder.putExtra("order_list", mOrderList);
                showOrder.putExtra("order_total", mOrderTotal);
                startActivity(showOrder);
            });

            restoreUserSession();
        }
        else if (getIntent().hasExtra("start_new_session")) {
            if (getIntent().hasExtra("table_id")
                    && getIntent().hasExtra("restaurant_id")) {

                mRestName = findViewById(R.id.text_pub_name);
                mTableSerial = findViewById(R.id.text_table);

                mBottomSheetOrder = findViewById(R.id.bottom_sheet_order);
                mBottomSheetOrder.setVisibility(View.GONE);

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

        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("table_id", tableID);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            sessionDetails.put("created_by", FirebaseAuth.getInstance().getCurrentUser().getUid());

        sessionDetails.put("start_time", new Timestamp(System.currentTimeMillis()));
        sessionDetails.put("is_active", true);

        firebaseFirestore.collection("restaurants")
                .document(restID)
                .collection("sessions")
                .add(sessionDetails)
                .addOnSuccessListener(documentReference -> {
                    final String sessionID = documentReference.getId();
                    Log.e("", "DocumentSnapshot written with ID: " + documentReference.getId());

                    Map<String, Object> userSessionDetails = new HashMap<>();
                    userSessionDetails.put("session_active", true);
                    userSessionDetails.put("current_session", sessionID);
                    userSessionDetails.put("restaurant", restID);

                    sharedPreferences.edit()
                            .putBoolean("session_active", true)
                            .apply();

                    firebaseFirestore.collection("users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .set(userSessionDetails, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Map<String, Object> tableSessionDetails = new HashMap<>();
                                tableSessionDetails.put("occupied", true);
                                tableSessionDetails.put("session_id", sessionID);

                                firebaseFirestore.collection("restaurants")
                                        .document(restID)
                                        .collection("tables")
                                        .document(tableID)
                                        .set(tableSessionDetails, SetOptions.merge())
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
        mMenuListViewAdapter = new MenuItemListAdapter(mMenu, mCategories, this);
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
                        mCategories.put(document.getId(), document.getString("name"));
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
                            mMenu.add(currentCategory);
                        }
                        String name = document.getString("name");
                        int cost = Integer.parseInt(Objects.requireNonNull(document.getString("cost")));
                        MenuItem item = new MenuItem(document.getId(), name, name, cost);
                        mMenu.add(item);
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
        mOrderTotal += item.getPrice();
        mBottomSheetOrder.setVisibility(View.VISIBLE);
        mOrderTotalText.setText(String.valueOf(mOrderTotal));

        if (mOrderList.size() == 0) {
            mOrderList.put(item, new MutableInt());
        }
        else {
            boolean updatedItem = false;
            for (Map.Entry<MenuItem, MutableInt> entry : mOrderList.entrySet()) {
                if (entry.getKey().getId().equals(item.getId())) {
                    MutableInt count = entry.getValue();
                    count.increment();
                    updatedItem = true;
                }
            }
            if (!updatedItem) {
                mOrderList.put(item, new MutableInt());
            }
        }

        mRecyclerMenuListView.setPadding(0, 0, 0, 0);
        mRecyclerMenuListView.setPadding(0, 0, 0, 225);
        mRecyclerMenuListView.setClipToPadding(false);
    }

    @Override
    public void removeFromOrder(MenuItem item) {
        mOrderTotal -= item.getPrice();

        for (Map.Entry<MenuItem, MutableInt> entry : mOrderList.entrySet()) {
            if (entry.getKey().getId().equals(item.getId())) {
                MutableInt count = entry.getValue();
                count.decrement();
                if (count.getValue() == 0) {
                    mOrderList.remove(entry.getKey());
                }
                break;
            }
        }

        if (mOrderTotal > 0) {
            mOrderTotalText.setText(String.valueOf(mOrderTotal));
        }
        else {
            mBottomSheetOrder.setVisibility(View.GONE);
            mRecyclerMenuListView.setPadding(0, 0, 0, 0);
            mRecyclerMenuListView.setClipToPadding(false);
        }
    }
}
