package com.frisky.icebreaker.orders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.frisky.icebreaker.ui.components.dialogs.ConfirmOrderDialog;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
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
    CartListAdapter mCartListAdapter;

    ArrayList<MenuItem> mCartList = new ArrayList<>();

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
        mTableSerial.setText(sharedPreferences.getString("table_name", ""));

        if (getIntent().hasExtra("cart_list")) {
            mCartList = (ArrayList<MenuItem>) getIntent().getSerializableExtra("cart_list");
        }

        for (int i = 0; i < mCartList.size(); i++) {
            Log.d(getString(R.string.tag_debug), "List " + mCartList.get(i).getName() + " " +
                    mCartList.get(i).getCount());
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

    private void confirmOrder() {
        ConfirmOrderDialog confirmOrderDialog = new ConfirmOrderDialog();
        confirmOrderDialog.show(getSupportFragmentManager(), "confirm order dialog");
    }

    @Override
    public void confirmOrder(boolean choice) {
        if (choice) {

            HashMap<String, Integer> order = new HashMap<>();

            ArrayList<OrderItem> orderListFinal = new ArrayList<>();

            for (int i = 0; i < mCartList.size(); i++) {
                MenuItem item = mCartList.get(i);
                order.put(item.getId(), item.getCount());
                OrderItem orderItem = new OrderItem(item.getId(), item.getName(),
                        item.getCount(), (item.getPrice() * item.getCount()));
                orderListFinal.add(orderItem);
            }

            placeOrder(order, orderListFinal);
        }
    }

    private void placeOrder(HashMap<String, Integer> orderList,  ArrayList<OrderItem> orderListFinal) {
        Map<String, Object> data = new HashMap<>();
        data.put("order", orderList);

        mFunctions
                .getHttpsCallable("placeOrder")
                .call(data)
                .continueWith(task -> {
                    if(task.isSuccessful()) {
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);

                        Intent notificationIntent = new Intent(this, OrderActivity.class);
                        notificationIntent.putExtra("order_ack", true);

                        PendingIntent pendingIntent =
                                PendingIntent.getActivity(this, 0, notificationIntent, 0);

                        NotificationCompat.Builder builder = new
                                NotificationCompat.Builder(this, getString(R.string.n_channel_orders))
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("Order Placed")
                                .setContentText("Your order has been placed. Awaiting Confirmation.")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                // Set the intent that will fire when the user taps the notification
                                .setContentIntent(pendingIntent);

                        notificationManager.notify(R.integer.n_order_session_service, builder.build());

                        Intent showOrder = new Intent(this, OrderActivity.class);
                        showOrder.putExtra("order_list", orderListFinal);
                        startActivity(showOrder);
                        finish();
                    }
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

        for (int i = 0; i < mCartList.size(); i++) {
            Log.d(getString(R.string.tag_debug), mCartList.get(i).getName() + " " +
                    mCartList.get(i).getCount());
        }
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
}
