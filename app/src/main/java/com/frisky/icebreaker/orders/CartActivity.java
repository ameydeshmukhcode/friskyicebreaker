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
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.frisky.icebreaker.ui.components.dialogs.ConfirmOrderDialog;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity implements UIActivity,
        ConfirmOrderDialog.OnConfirmOrderListener {

    Button mConfirmOrderButton;
    ImageButton mBackButton;

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

        TextView mTotal = findViewById(R.id.text_order_total);

        TextView mTableSerial = findViewById(R.id.text_table);
        if (getIntent().hasExtra("table_id")){
            mTableSerial.setText(getIntent().getStringExtra("table_id"));
        }

        if (getIntent().hasExtra("cart_list")) {
            mCartList = (HashMap<MenuItem, MutableInt>) getIntent().getSerializableExtra("cart_list");
        }

        for (Map.Entry<MenuItem, MutableInt> entry : mCartList.entrySet()) {
            Log.d("List", entry.getKey().getName() + " " + entry.getValue().getValue());
        }

        if (getIntent().hasExtra("cart_total")) {
            mTotal.setText(String.valueOf(getIntent().getIntExtra("cart_total", 0)));
        }

        mRecyclerCartListView = findViewById(R.id.recycler_view_cart_list);
        mRecyclerCartListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerView.LayoutManager mMenuListViewLayoutManager;
        mMenuListViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerCartListView.setLayoutManager(mMenuListViewLayoutManager);

        // specify an adapter (see also next example)
        CartListAdapter cartListAdapter = new CartListAdapter(getApplicationContext(), mCartList);
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

            HashMap<String, OrderStatus> orderListFinal = new HashMap<>();

            for (Map.Entry<MenuItem, MutableInt> entry: mCartList.entrySet()) {
                order.put(entry.getKey().getId(), entry.getValue().getValue());
                orderListFinal.put(entry.getKey().getId(), OrderStatus.PENDING);
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
}
