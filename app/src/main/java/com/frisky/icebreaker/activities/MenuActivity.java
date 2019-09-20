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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.MenuItemListAdapter;
import com.frisky.icebreaker.core.structures.MenuCategory;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.interfaces.OnOrderUpdateListener;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuActivity extends AppCompatActivity implements UIActivity,
        OnOrderUpdateListener {

    SharedPreferences sharedPreferences;

    Button mBackButton;
    TextView mRestName;
    TextView mTableSerial;

    private List<Object> mMenu = new ArrayList<>();
    private List<MenuCategory> mCategories = new ArrayList<>();
    String restaurantID;

    int mCartTotal = 0;
    ArrayList<MenuItem> mCartList = new ArrayList<>();

    RecyclerView.Adapter mMenuListViewAdapter;
    RecyclerView mRecyclerMenuListView;

    ConstraintLayout mBottomSheetCart;
    ConstraintLayout mBottomSheetOrder;
    ConstraintLayout mDummyMenu;
    TextView mCartTotalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        setContentView(R.layout.activity_menu);

        restaurantID = sharedPreferences.getString("restaurant_id", "");

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

        mDummyMenu = findViewById(R.id.dummy_menu);

        mRestName = findViewById(R.id.text_pub_name);
        mTableSerial = findViewById(R.id.text_table);
        mRecyclerMenuListView = findViewById(R.id.recycler_view);

        mBottomSheetCart = findViewById(R.id.bottom_sheet_cart);
        mBottomSheetCart.setVisibility(View.GONE);

        mBottomSheetOrder = findViewById(R.id.bottom_sheet_order);

        if (sharedPreferences.getBoolean("order_active", false)) {
            mBottomSheetOrder.setVisibility(View.VISIBLE);
            mBottomSheetOrder.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), OrderActivity.class)));
            mRecyclerMenuListView.setPadding(0, 0, 0, 0);
            mRecyclerMenuListView.setPadding(0, 0, 0, 225);
            mRecyclerMenuListView.setClipToPadding(false);
        }
        else {
            mBottomSheetOrder.setVisibility(View.GONE);
        }

        mCartTotalText = findViewById(R.id.text_order_amount);
        mBottomSheetCart.setOnClickListener(v -> {
            Intent showOrder = new Intent(getApplicationContext(), CartActivity.class);
            showOrder.putExtra("cart_list", mCartList);
            showOrder.putExtra("cart_total", mCartTotal);
            startActivity(showOrder);
        });

        setUserSession();
    }

    private void setUserSession() {
        mRestName.setText(sharedPreferences.getString("restaurant_name", ""));
        mTableSerial.setText(sharedPreferences.getString("table_name", ""));
        setMenu();
    }

    private void setMenu() {
        mRecyclerMenuListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerView.LayoutManager mMenuListViewLayoutManager;
        // use a linear layout manager
        mMenuListViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerMenuListView.setLayoutManager(mMenuListViewLayoutManager);

        // specify an adapter (see also next example)
        mMenuListViewAdapter = new MenuItemListAdapter(mMenu, this);
        mRecyclerMenuListView.setAdapter(mMenuListViewAdapter);

        prepareMenuData();
    }

    private void prepareMenuData() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("restaurants")
                .document(restaurantID)
                .collection("categories")
                .orderBy("order")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    for (DocumentSnapshot document : task.getResult()) {
                        MenuCategory menuCategory = new MenuCategory(document.getId(),  document.getString("name"));
                        mCategories.add(menuCategory);
                    }
                    int current = 0;
                    getMenuCategory(current);
                }
            }
        });
    }

    private void getMenuCategory(int index) {
        if (index > mCategories.size() - 1) {
            return;
        }

        final MenuCategory menuCategory = mCategories.get(index);

        FirebaseFirestore.getInstance()
                .collection("restaurants").document(restaurantID)
                .collection("items")
                .whereEqualTo("category_id", menuCategory.getId())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    mMenu.add(menuCategory);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        boolean available = true;
                        String name = document.getString("name");
                        if (document.contains("is_available")) {
                            if (!(boolean) document.get("is_available")) {
                                available = false;
                            }
                        }
                        String description = "";
                        if (document.contains("description")) {
                            description = document.getString("description");
                        }
                        int cost = Integer.parseInt(Objects.requireNonNull(document.getString("cost")));
                        MenuItem item = new MenuItem(document.getId(), name, description, cost, available);
                        mMenu.add(item);
                        mMenuListViewAdapter.notifyDataSetChanged();
                        mDummyMenu.setVisibility(View.GONE);
                    }
                    getMenuCategory(index + 1);
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
                if (mCartList.get(i).getCount() == 0) {
                    mCartList.remove(i);
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
                mBottomSheetOrder.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), OrderActivity.class)));
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
