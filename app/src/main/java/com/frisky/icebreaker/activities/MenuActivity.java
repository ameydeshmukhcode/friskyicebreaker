package com.frisky.icebreaker.activities;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.MenuItemListAdapter;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.interfaces.OnOrderUpdateListener;
import com.frisky.icebreaker.services.OrderSessionService;
import com.frisky.icebreaker.interfaces.UIActivity;
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
        OnOrderUpdateListener {

    SharedPreferences sharedPreferences;

    ImageButton mBackButton;
    TextView mRestName;
    TextView mTableSerial;

    private List<Object> mMenu = new ArrayList<>();
    private HashMap<String, String> mCategories = new HashMap<>();

    int mCartTotal = 0;
    ArrayList<MenuItem> mCartList = new ArrayList<>();

    RecyclerView.Adapter mMenuListViewAdapter;
    RecyclerView mRecyclerMenuListView;

    ConstraintLayout mBottomSheetCart;
    ConstraintLayout mBottomSheetOrder;
    TextView mCartTotalText;
    Button mViewCartButton;

    boolean isSessionActive;
    boolean startNewSession = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        isSessionActive = getSharedPreferences(getString(R.string.app_name),
                MODE_PRIVATE).getBoolean("session_active", false);

        if (getIntent().hasExtra("start_new_session")) {
            startNewSession = true;
        }

        if (isSessionActive || startNewSession) {
            setContentView(R.layout.activity_menu);
        }
        else {
            setContentView(R.layout.activity_menu_empty_state);
        }

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

        initUI();
    }

    @Override
    public void initUI() {
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> MenuActivity.super.onBackPressed());

        if (isSessionActive) {
            mRestName = findViewById(R.id.text_pub_name);
            mTableSerial = findViewById(R.id.text_table);
            mRecyclerMenuListView = findViewById(R.id.recycler_view);

            mBottomSheetCart = findViewById(R.id.bottom_sheet_cart);
            mBottomSheetCart.setVisibility(View.GONE);

            mBottomSheetOrder = findViewById(R.id.bottom_sheet_order);

            if (sharedPreferences.getBoolean("order_active", false)) {
                mBottomSheetOrder.setVisibility(View.VISIBLE);
                mBottomSheetOrder.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), OrderActivity.class));
                });
                mRecyclerMenuListView.setPadding(0, 0, 0, 0);
                mRecyclerMenuListView.setPadding(0, 0, 0, 225);
                mRecyclerMenuListView.setClipToPadding(false);
            }
            else {
                mBottomSheetOrder.setVisibility(View.GONE);
            }

            mCartTotalText = findViewById(R.id.text_order_amount);
            mViewCartButton = findViewById(R.id.button_view_order);
            mViewCartButton.setOnClickListener(v -> {
                Intent showOrder = new Intent(getApplicationContext(), CartActivity.class);
                showOrder.putExtra("cart_list", mCartList);
                showOrder.putExtra("cart_total", mCartTotal);
                startActivity(showOrder);
            });

            restoreUserSession();
        }
        else if (startNewSession) {
            if (getIntent().hasExtra("table_id")
                    && getIntent().hasExtra("restaurant_id")) {

                mRestName = findViewById(R.id.text_pub_name);
                mTableSerial = findViewById(R.id.text_table);

                mRecyclerMenuListView = findViewById(R.id.recycler_view);

                mBottomSheetCart = findViewById(R.id.bottom_sheet_cart);
                mBottomSheetCart.setVisibility(View.GONE);

                mBottomSheetOrder = findViewById(R.id.bottom_sheet_order);
                mBottomSheetOrder.setVisibility(View.GONE);

                mCartTotalText = findViewById(R.id.text_order_amount);

                final String restID = getIntent().getStringExtra("restaurant_id");
                final String tableID = getIntent().getStringExtra("table_id");

                mViewCartButton = findViewById(R.id.button_view_order);
                mViewCartButton.setOnClickListener(v -> {
                    Intent showOrder = new Intent(getApplicationContext(), CartActivity.class);
                    showOrder.putExtra("cart_list", mCartList);
                    showOrder.putExtra("cart_total", mCartTotal);
                    startActivity(showOrder);
                });

                getRestaurantAndTableDetails(restID, tableID);
                initUserSession(restID, tableID);
                setMenu(restID);
            }
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
                    String restaurantName = document.getString("name");
                    mRestName.setText(restaurantName);
                    sharedPreferences.edit().putString("restaurant_name", restaurantName).apply();
                    Log.d(getString(R.string.tag_debug), "DocumentSnapshot data: " + document.getData());
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
                    sharedPreferences.edit().putString("table_name", tableSerial).apply();
                    Log.d(getString(R.string.tag_debug), "DocumentSnapshot data: " + document.getData());
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
                    Log.d(getString(R.string.tag_debug), "DocumentSnapshot written with ID: " + documentReference.getId());

                    Map<String, Object> userSessionDetails = new HashMap<>();
                    userSessionDetails.put("session_active", true);
                    userSessionDetails.put("current_session", sessionID);
                    userSessionDetails.put("restaurant", restID);

                    sharedPreferences.edit()
                            .putBoolean("session_active", true)
                            .putString("restaurant_id", restID)
                            .putString("session_id", sessionID)
                            .putString("table_id", tableID)
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
                                        .addOnSuccessListener(aVoid1 -> {
                                            Log.d(getString(R.string.tag_debug), "Table details updated");
                                            Intent orderSession = new Intent(getApplicationContext(), OrderSessionService.class);
                                            startService(orderSession);
                                        });
                            });
                })
                .addOnFailureListener(e -> Log.e("", "Error adding document", e));
    }

    private void restoreUserSession() {
        mRestName.setText(sharedPreferences.getString("restaurant_name", ""));
        mTableSerial.setText(sharedPreferences.getString("table_name", ""));
        setMenu(sharedPreferences.getString("restaurant_id", ""));
    }

    private void setMenu(String restaurant) {
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
                        boolean available = true;
                        String name = document.getString("name");
                        if (document.contains("is_available")) {
                            if (!(boolean) document.get("is_available")) {
                                available = false;
                            }
                        }
                        int cost = Integer.parseInt(Objects.requireNonNull(document.getString("cost")));
                        MenuItem item = new MenuItem(document.getId(), name, name + " description", cost, available);
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
        mBottomSheetOrder.setVisibility(View.GONE);

        mCartTotal += item.getPrice();
        mBottomSheetCart.setVisibility(View.VISIBLE);
        mCartTotalText.setText(String.valueOf(mCartTotal));

        if (mCartList.size() == 0) {
            mCartList.add(item);
        }
        else {
            boolean updatedItem = false;
            for (int i = 0; i < mCartList.size(); i++) {
                if (mCartList.get(i).getId().equals(item.getId())) {
                    mCartList.get(i).incrementCount();
                    updatedItem = true;
                    break;
                }
            }
            if (!updatedItem) {
                mCartList.add(item);
            }
        }

        for (int i = 0; i < mCartList.size(); i++) {
            Log.d(getString(R.string.tag_debug), mCartList.get(i).getName() + " " +
                    mCartList.get(i).getCount());
        }

        mRecyclerMenuListView.setPadding(0, 0, 0, 0);
        mRecyclerMenuListView.setPadding(0, 0, 0, 225);
        mRecyclerMenuListView.setClipToPadding(false);
    }

    @Override
    public void removeFromOrder(MenuItem item) {
        mCartTotal -= item.getPrice();

        for (int i = 0; i < mCartList.size(); i++) {
            if (mCartList.get(i).getId().equals(item.getId())) {
                if (mCartList.get(i).getCount() == 1) {
                    mCartList.remove(i);
                }
                else {
                    mCartList.get(i).decrementCount();
                }
                break;
            }
        }

        if (mCartTotal > 0) {
            mCartTotalText.setText(String.valueOf(mCartTotal));
        }
        else {
            if (sharedPreferences.getBoolean("order_active", false)) {
                mBottomSheetOrder.setVisibility(View.VISIBLE);
                mBottomSheetOrder.setOnClickListener(v -> {
                    startActivity(new Intent(getApplicationContext(), OrderActivity.class));
                });
                mBottomSheetCart.setVisibility(View.GONE);
            }
            else {
                mBottomSheetOrder.setVisibility(View.GONE);
                mBottomSheetCart.setVisibility(View.GONE);
                mRecyclerMenuListView.setPadding(0, 0, 0, 0);
                mRecyclerMenuListView.setClipToPadding(false);
            }
        }
    }
}
