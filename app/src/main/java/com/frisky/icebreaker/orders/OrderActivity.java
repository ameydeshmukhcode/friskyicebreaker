package com.frisky.icebreaker.orders;

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
import com.frisky.icebreaker.ui.base.UIActivity;
import com.frisky.icebreaker.ui.components.dialogs.ConfirmOrderDialog;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity implements UIActivity,
        ConfirmOrderDialog.OnConfirmOrderListener {

    Button mConfirmOrderButton;
    Button mAddMoreButton;
    ImageButton mBackButton;
    HashMap<MenuItem, MutableInt> mCartList = new HashMap<>();

    private FirebaseFunctions mFunctions;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mFunctions = FirebaseFunctions.getInstance();
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        initUI();

        addListenerForOrderUpdates();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initUI() {
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> OrderActivity.super.onBackPressed());

        mConfirmOrderButton = findViewById(R.id.button_confirm_order);
        mConfirmOrderButton.setOnClickListener(v -> {
            if (mCartList.size() > 0)
                confirmOrder();
        });

        mAddMoreButton = findViewById(R.id.button_add_more);

        TextView mTotal = findViewById(R.id.text_order_total);

        TextView mTableSerial = findViewById(R.id.text_table);
        if (getIntent().hasExtra("table_id")){
            mTableSerial.setText(getIntent().getStringExtra("table_id"));
        }

        if (getIntent().hasExtra("order_list")) {
            mCartList = (HashMap<MenuItem, MutableInt>) getIntent().getSerializableExtra("order_list");
        }

        for (Map.Entry<MenuItem, MutableInt> entry : mCartList.entrySet()) {
            Log.i("List", entry.getKey().getName() + " " + entry.getValue().getValue());
        }

        if (getIntent().hasExtra("order_total")) {
            mTotal.setText(String.valueOf(getIntent().getIntExtra("order_total", 0)));
        }

        RecyclerView mRecyclerCartListView = findViewById(R.id.recycler_view_cart_list);
        mRecyclerCartListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerView.LayoutManager mMenuListViewLayoutManager;
        // use a linear layout manager
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

            HashMap<String, Integer> orderList = new HashMap<>();

            for (Map.Entry<MenuItem, MutableInt> entry : mCartList.entrySet()) {
                orderList.put(entry.getKey().getId(), entry.getValue().getValue());
            }

            for (Map.Entry<String, Integer> entry : orderList.entrySet()) {
                Log.i(entry.getKey(), String.valueOf(entry.getValue()));
            }

            placeOrder(orderList);
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

    private void addListenerForOrderUpdates() {
        String restaurant = "";
        String currentSession = "";
        if (sharedPreferences.contains("restaurant")) {
            restaurant = sharedPreferences.getString("restaurant", "");
        }
        if (sharedPreferences.contains("current_session")) {
            currentSession = sharedPreferences.getString("current_session", "");
        }

        assert restaurant != null;
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("restaurants")
                .document(restaurant);

        docRef.collection("orders")
                .whereEqualTo("session_id", currentSession)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        System.err.println("Listen failed: " + e);
                        return;
                    }

                    assert queryDocumentSnapshots != null;
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("Added", "to Orders");
                                break;
                            case MODIFIED:
                                Log.d("Modified", "Orders");
                                break;
                            case REMOVED:
                                Log.d("Removed", "from Orders");
                                break;
                            default:
                                break;
                        }
                    }
                });
    }
}
