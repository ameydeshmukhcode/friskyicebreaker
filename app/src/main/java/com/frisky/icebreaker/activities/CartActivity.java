package com.frisky.icebreaker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.CartListAdapter;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.interfaces.OrderUpdateListener;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.frisky.icebreaker.ui.components.dialogs.ConfirmOrderDialog;
import com.frisky.icebreaker.ui.components.dialogs.ProgressDialog;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity implements UIActivity,
        ConfirmOrderDialog.OnConfirmOrderListener, OrderUpdateListener {

    FirebaseFunctions mFunctions;

    SharedPreferences sharedPreferences;

    Button mPlaceOrderButton;
    Button mBackButton;
    TextView mCartTotalText;

    RecyclerView mRecyclerCartListView;
    CartListAdapter mCartListAdapter;

    ProgressDialog progressDialog = new ProgressDialog("Placing your order");

    int mCartTotal;
    ArrayList<MenuItem> mCartList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        mFunctions = FirebaseFunctions.getInstance();

        initUI();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initUI() {
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> CartActivity.super.onBackPressed());

        mPlaceOrderButton = findViewById(R.id.button_place_order);
        mPlaceOrderButton.setOnClickListener(v -> {
            if (mCartList.size() > 0) {
                ConfirmOrderDialog confirmOrderDialog = new ConfirmOrderDialog();
                confirmOrderDialog.show(getSupportFragmentManager(), "confirm order dialog");
            }
        });

        mCartTotalText = findViewById(R.id.text_cart_total);

        TextView mTableSerial = findViewById(R.id.text_table);
        mTableSerial.setText(sharedPreferences.getString("table_name", ""));

        if (getIntent().hasExtra("cart_list")) {
            mCartList = (ArrayList<MenuItem>) getIntent().getSerializableExtra("cart_list");
        }

        if (getIntent().hasExtra("cart_total")) {
            int cartTotal = getIntent().getIntExtra("cart_total", 0);
            mCartTotalText.setText(String.valueOf(cartTotal));
            mCartTotal = cartTotal;
        }

        mRecyclerCartListView = findViewById(R.id.recycler_view_cart_list);
        mRecyclerCartListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerView.LayoutManager mMenuListViewLayoutManager;
        mMenuListViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerCartListView.setLayoutManager(mMenuListViewLayoutManager);

        // specify an adapter (see also next example)
        mCartListAdapter = new CartListAdapter(mCartList, this);
        mRecyclerCartListView.setAdapter(mCartListAdapter);
    }

    @Override
    public void addToOrder(MenuItem item) {
        mCartTotal += item.getPrice();
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

        mCartListAdapter.notifyDataSetChanged();
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
                mCartListAdapter.notifyDataSetChanged();
                break;
            }
        }

        mCartTotalText.setText(String.valueOf(mCartTotal));
        if (mCartTotal == 0) {
            super.onBackPressed();
        }
    }

    @Override
    public void confirmOrder(boolean choice) {
        if (choice) {

            HashMap<String, Integer> order = new HashMap<>();

            for (int i = 0; i < mCartList.size(); i++) {
                MenuItem item = mCartList.get(i);
                order.put(item.getId(), item.getCount());
            }

            progressDialog.show(getSupportFragmentManager(), "progress dialog");
            progressDialog.setCancelable(false);
            placeOrder(order);
        }
    }

    private void placeOrder(HashMap<String, Integer> orderList) {
        Map<String, Object> data = new HashMap<>();
        data.put("order", orderList);

        mFunctions
                .getHttpsCallable("placeOrder")
                .call(data)
                .continueWith(task -> {
                    if(task.isSuccessful()) {
                        sharedPreferences.edit().putBoolean("order_active", true).apply();

                        Intent showOrder = new Intent(this, OrderActivity.class);
                        startActivity(showOrder);
                        finish();
                    }
                    else if (!task.isSuccessful()){
                        progressDialog.dismiss();
                    }
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    return (String) Objects.requireNonNull(task.getResult()).getData();
                });
    }
}
