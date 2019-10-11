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
import com.frisky.icebreaker.ui.assistant.NonDismissibleSnackBar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
    ExtendedFloatingActionButton mCategoryPickerButton;
    ConstraintLayout mDummyMenu;

    RecyclerView.Adapter mMenuListViewAdapter;
    RecyclerView mRecyclerMenuListView;

    Snackbar mSnackbar;

    int mCartTotal = 0;
    boolean mSnackbarVisible = false;
    String restaurantID;
    private List<Object> mMenu = new ArrayList<>();
    private List<MenuCategory> mCategories = new ArrayList<>();
    ArrayList<MenuItem> mCartList = new ArrayList<>();
    HashMap<String, ArrayList<MenuItem>> mItems = new HashMap<>();
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
        mCategoryPickerButton.setElevation(8);

        mDummyMenu = findViewById(R.id.dummy_menu);

        mRestName = findViewById(R.id.text_restaurant_name);
        mTableSerial = findViewById(R.id.text_table);
        mRecyclerMenuListView = findViewById(R.id.recycler_view_menu);

        setUserSession();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean("order_active", false)) {
            showOrderSnackbar();

            mRecyclerMenuListView.setPadding(0, 0, 0, 0);
            mRecyclerMenuListView.setPadding(0, 0, 0, 225);
            mRecyclerMenuListView.setClipToPadding(false);
        }

        if (mCartList.size() > 0) showCartSnackbar();
    }

    @Override
    public void addToOrder(MenuItem item) {
        mCartTotal += item.getPrice();

        if (!mSnackbarVisible) {
            showCartSnackbar();
        }

        mSnackbarVisible = true;

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

        if (mCartTotal <= 0) {
            if (mSnackbarVisible) {
                mSnackbar.dismiss();
                mSnackbarVisible = false;
            }

            if (sharedPreferences.getBoolean("order_active", false)) {
                showOrderSnackbar();
            } else {
                mRecyclerMenuListView.setPadding(0, 0, 0, 100);
                mRecyclerMenuListView.setClipToPadding(false);
            }
        }
    }

    private void setUserSession() {
        mRestName.setText(sharedPreferences.getString("restaurant_name", ""));
        mTableSerial.setText(sharedPreferences.getString("table_name", ""));
        mRecyclerMenuListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerView.LayoutManager mMenuListViewLayoutManager;
        mMenuListViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerMenuListView.setLayoutManager(mMenuListViewLayoutManager);

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
                    String categoryId = "";
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        boolean available = true;
                        String currentCategory = document.getString("category_id");

                        if (!categoryId.equals(currentCategory)) categoryId = document.getString("category_id");

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
                                currentCategory, cost, available, type);

                        if (!mItems.containsKey(currentCategory)) {
                            ArrayList<MenuItem> categoryList = new ArrayList<>();
                            categoryList.add(item);
                            mItems.put(currentCategory, categoryList);
                        } else {
                            mItems.get(currentCategory).add(item);
                        }
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
            String categoryID = category.getId();
            mMenu.add(category);
            mCategoryOrderMap.put(category.getName(), mMenu.size() - 1);
            categoryMenu.getMenu().add(category.getName());
            mMenu.addAll(mItems.get(categoryID));
        }
        mMenuListViewAdapter.notifyDataSetChanged();
        mDummyMenu.setVisibility(View.GONE);
    }

    private void showOrderSnackbar() {
        View root = findViewById(R.id.root);
        mSnackbar = Snackbar.make(root, R.string.view_order_summary, Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setBehavior(new NonDismissibleSnackBar());
        mSnackbar.setAction("View", v -> startActivity(new Intent(getApplicationContext(), OrderActivity.class)));
        mSnackbar.show();
    }

    private void showCartSnackbar() {
        View root = findViewById(R.id.root);
        mSnackbar = Snackbar.make(root, R.string.cart_active, Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setBehavior(new NonDismissibleSnackBar());
        mSnackbar.setAction("View", v -> {
            Intent showOrder = new Intent(getApplicationContext(), CartActivity.class);
            showOrder.putExtra("cart_list", mCartList);
            showOrder.putExtra("cart_total", mCartTotal);
            startActivity(showOrder);
        });
        mSnackbar.show();
    }
}
