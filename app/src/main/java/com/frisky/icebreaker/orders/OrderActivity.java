package com.frisky.icebreaker.orders;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UIActivity;

public class OrderActivity extends AppCompatActivity implements UIActivity {

    ImageButton mBackButton;

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

        TextView mTableSerial = findViewById(R.id.text_table);
        if (getIntent().hasExtra("table_id")){
            mTableSerial.setText(getIntent().getStringExtra("table_id"));
        }
    }
}
