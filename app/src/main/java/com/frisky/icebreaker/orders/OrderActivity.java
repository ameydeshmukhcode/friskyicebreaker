package com.frisky.icebreaker.orders;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    HashMap<MenuItem, MutableInt> orderList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initUI();
    }

    @Override
    public void initUI() {
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> OrderActivity.super.onBackPressed());

        TextView mTotal = findViewById(R.id.text_order_total);

        TextView mTableSerial = findViewById(R.id.text_table);
        if (getIntent().hasExtra("table_id")){
            mTableSerial.setText(getIntent().getStringExtra("table_id"));
        }

        if (getIntent().hasExtra("order_list")) {
            orderList = (HashMap<MenuItem, MutableInt>) getIntent().getSerializableExtra("order_list");
        }

        for (Map.Entry<MenuItem, MutableInt> entry : orderList.entrySet()) {
            Log.i("List", entry.getKey().getName() + " " + entry.getValue().getValue());
        }
    }
}
