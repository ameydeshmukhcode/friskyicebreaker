package com.frisky.icebreaker.orders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.core.structures.MutableInt;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.frisky.icebreaker.ui.components.dialogs.ConfirmOrderDialog;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity implements UIActivity,
        ConfirmOrderDialog.OnConfirmOrderListener, OnOrderUpdateListener {

    Button mConfirmOrderButton;
    ImageButton mBackButton;
    TextView mCartTotalText;
    int mCartTotal;

    RecyclerView mRecyclerCartListView;

    HashMap<MenuItem, MutableInt> mCartList = new HashMap<>();

    SharedPreferences sharedPreferences;

    FirebaseFunctions mFunctions;

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

        mConfirmOrderButton = findViewById(R.id.button_confirm_order);
        mConfirmOrderButton.setOnClickListener(v -> {
            if (mCartList.size() > 0)
                confirmOrder();
        });

        mCartTotalText = findViewById(R.id.text_order_total);

        TextView mTableSerial = findViewById(R.id.text_table);
        if (getIntent().hasExtra("table_id")){
            mTableSerial.setText(getIntent().getStringExtra("table_id"));
        }

        if (getIntent().hasExtra("cart_list")) {
            mCartList = (HashMap<MenuItem, MutableInt>) getIntent().getSerializableExtra("cart_list");
        }

        for (Map.Entry<MenuItem, MutableInt> entry : mCartList.entrySet()) {
            Log.d(getString(R.string.tag_debug), "List " + entry.getKey().getName() + " " + entry.getValue().getValue());
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
        CartListAdapter cartListAdapter = new CartListAdapter(getApplicationContext(), mCartList, this);
        mRecyclerCartListView.setAdapter(cartListAdapter);
    }

    private void confirmOrder() {
        ConfirmOrderDialog confirmOrderDialog = new ConfirmOrderDialog();
        confirmOrderDialog.show(getSupportFragmentManager(), "confirm order dialog");
    }

    @Override
    public void confirmOrder(boolean choice) {
        if (choice) {

            HashMap<String, Integer> order = new HashMap<>();

            HashMap<OrderItem, OrderStatus> orderListFinal = new HashMap<>();

            for (Map.Entry<MenuItem, MutableInt> entry: mCartList.entrySet()) {
                order.put(entry.getKey().getId(), entry.getValue().getValue());
                MenuItem menuItem = entry.getKey();
                MutableInt itemCount = entry.getValue();
                OrderItem orderItem = new OrderItem(menuItem.getId(), menuItem.getName(),
                        itemCount.getValue(), (menuItem.getPrice() * itemCount.getValue()));
                orderListFinal.put(orderItem, OrderStatus.PENDING);
            }

            placeOrder(order);

            Intent showOrder = new Intent(this, OrderActivity.class);
            showOrder.putExtra("order_list", orderListFinal);
            startActivity(showOrder);
        }
    }

    private void placeOrder(HashMap<String, Integer> orderList) {
        Map<String, Object> data = new HashMap<>();
        data.put("order", orderList);

        mFunctions
                .getHttpsCallable("placeOrder")
                .call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    return (String) Objects.requireNonNull(task.getResult()).getData();
                });
    }

    @Override
    public void addToOrder(MenuItem item) {
        mCartTotal += item.getPrice();
        mCartTotalText.setText(String.valueOf(mCartTotal));

        if (mCartList.size() == 0) {
            mCartList.put(item, new MutableInt());
        }
        else {
            boolean updatedItem = false;
            for (Map.Entry<MenuItem, MutableInt> entry : mCartList.entrySet()) {
                if (entry.getKey().getId().equals(item.getId())) {
                    MutableInt count = entry.getValue();
                    count.increment();
                    updatedItem = true;
                }
            }
            if (!updatedItem) {
                mCartList.put(item, new MutableInt());
            }
        }
    }

    @Override
    public void removeFromOrder(MenuItem item) {
        mCartTotal -= item.getPrice();

        for (Map.Entry<MenuItem, MutableInt> entry : mCartList.entrySet()) {
            if (entry.getKey().getId().equals(item.getId())) {
                MutableInt count = entry.getValue();
                count.decrement();
                if (count.getValue() == 0) {
                    mCartList.remove(entry.getKey());
                }
                break;
            }
        }
        mCartTotalText.setText(String.valueOf(mCartTotal));
        if (mCartTotal == 0) {
            super.onBackPressed();
        }
    }
}
