package com.frisky.icebreaker.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.MenuListAdapter;
import com.frisky.icebreaker.core.structures.DietType;
import com.frisky.icebreaker.core.structures.MenuCategory;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.interfaces.OrderUpdateListener;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.frisky.icebreaker.ui.assistant.UIAssistant.getDietTypeFromString;

public class MenuActivity extends AppCompatActivity implements UIActivity,
        OrderUpdateListener {

    SharedPreferences sharedPreferences;

    Button mBackButton;
    TextView mRestName;
    TextView mTableSerial;
    Button mCategoryPickerButton;
    ConstraintLayout mBottomSheetCart;
    ConstraintLayout mBottomSheetOrder;
    ConstraintLayout mDummyMenu;
    TextView mCartTotalText;

    RecyclerView.Adapter mMenuListViewAdapter;
    RecyclerView mRecyclerMenuListView;

    int mCartTotal = 0;
    String restaurantID;
    private List<Object> mMenu = new ArrayList<>();
    private List<MenuCategory> mCategories = new ArrayList<>();
    private List<MenuItem> mItems = new ArrayList<>();
    ArrayList<MenuItem> mCartList = new ArrayList<>();
    HashMap<String, Integer> mCategoryOrderMap = new HashMap<>();


    PopupMenu categoryMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        setContentView(R.layout.activity_menu);

        restaurantID = sharedPreferences.getString("restaurant_id", "");

        initUI();
    }

    @Override
    public void initUI() {
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> MenuActivity.super.onBackPressed());

        mCategoryPickerButton = findViewById(R.id.button_category);

        Context wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.FriskyPopUpMenu);
        categoryMenu = new PopupMenu(wrapper, mCategoryPickerButton);

        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        categoryMenu.setOnMenuItemClickListener(item -> {
            String categoryName = item.getTitle().toString();
            int categoryPosition = mCategoryOrderMap.get(categoryName);
            smoothScroller.setTargetPosition(categoryPosition);
            mRecyclerMenuListView.getLayoutManager().startSmoothScroll(smoothScroller);
            return true;
        });

        mCategoryPickerButton.setOnClickListener(v -> categoryMenu.show());

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
        } else {
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

    @Override
    public void addToOrder(MenuItem item) {
        mBottomSheetOrder.setVisibility(View.GONE);

        mCartTotal += item.getPrice();
        mBottomSheetCart.setVisibility(View.VISIBLE);
        mCartTotalText.setText(String.valueOf(mCartTotal));

        if (mCartList.size() == 0) {
            mCartList.add(item);
        } else {
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
        } else {
            mBottomSheetCart.setVisibility(View.GONE);
            if (sharedPreferences.getBoolean("order_active", false)) {
                mBottomSheetOrder.setVisibility(View.VISIBLE);
                mBottomSheetOrder.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), OrderActivity.class)));
            } else {
                mBottomSheetOrder.setVisibility(View.GONE);
                mRecyclerMenuListView.setPadding(0, 0, 0, 0);
                mRecyclerMenuListView.setClipToPadding(false);
            }
        }
    }

    private void setUserSession() {
        mRestName.setText(sharedPreferences.getString("restaurant_name", ""));
        mTableSerial.setText(sharedPreferences.getString("table_name", ""));
        mRecyclerMenuListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerView.LayoutManager mMenuListViewLayoutManager;
        // use a linear layout manager
        mMenuListViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerMenuListView.setLayoutManager(mMenuListViewLayoutManager);

        // specify an adapter (see also next example)
        mMenuListViewAdapter = new MenuListAdapter(mMenu, this);
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
                    getItems();
                }
            }
        });
    }

    private void getItems() {
        FirebaseFirestore.getInstance()
                .collection("restaurants").document(restaurantID)
                .collection("items")
                .orderBy("category_id")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
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
                        DietType type = DietType.NONE;
                        if (document.contains("type")) {
                            type = getDietTypeFromString(document.getString("type"));
                        }
                        int cost = Integer.parseInt(Objects.requireNonNull(document.getString("cost")));
                        MenuItem item = new MenuItem(document.getId(), name, description,
                                document.getString("category_id"), cost, available, type);
                        mItems.add(item);
                    }
                    setupMenu();
                }
            }
            else {
                Log.e("error", "Error getting documents: ", task.getException());
            }
        });
    }

    private void setupMenu() {
        for (int i = 0; i < mCategories.size(); i++) {
            final MenuCategory category = mCategories.get(i);
            mMenu.add(category);
            mCategoryOrderMap.put(category.getName(), mMenu.size() - 1);
            categoryMenu.getMenu().add(category.getName());
            for (int j = 0; j < mItems.size(); j++) {
                if (mItems.get(j).getCategory().matches(category.getId())) {
                    mMenu.add(mItems.get(j));
                }
            }
        }
        mMenuListViewAdapter.notifyDataSetChanged();
        mDummyMenu.setVisibility(View.GONE);
    }
}
