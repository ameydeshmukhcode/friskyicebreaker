package com.frisky.icebreaker.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.core.structures.MutableInt;
import com.frisky.icebreaker.ui.base.UIActivity;

import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements UIActivity {

    ImageButton mBackButton;
    HashMap<MenuItem, MutableInt> mOrderList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initUI();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initUI() {
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> OrderActivity.super.onBackPressed());

        TextView mTotal = findViewById(R.id.text_order_total);

        TextView mTableSerial = findViewById(R.id.text_table);
        if (getIntent().hasExtra("table_id")){
            mTableSerial.setText(getIntent().getStringExtra("table_id"));
        }

        if (getIntent().hasExtra("order_list")) {
            mOrderList = (HashMap<MenuItem, MutableInt>) getIntent().getSerializableExtra("order_list");
        }

        for (Map.Entry<MenuItem, MutableInt> entry : mOrderList.entrySet()) {
            Log.i("List", entry.getKey().getName() + " " + entry.getValue().getValue());
        }

        if (getIntent().hasExtra("order_total")) {
            mTotal.setText(String.valueOf(getIntent().getIntExtra("order_total", 0)));
        }

        RecyclerView mRecyclerOrderListView = findViewById(R.id.recycler_view_order_list);
        mRecyclerOrderListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerView.LayoutManager mMenuListViewLayoutManager;
        // use a linear layout manager
        mMenuListViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerOrderListView.setLayoutManager(mMenuListViewLayoutManager);

        // specify an adapter (see also next example)
        OrderListAdapter orderListAdapter = new OrderListAdapter(getApplicationContext(), mOrderList);
        mRecyclerOrderListView.setAdapter(orderListAdapter);
    }
}
