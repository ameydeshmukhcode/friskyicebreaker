package com.frisky.icebreaker.orders;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderActivity.super.onBackPressed();
            }
        });
    }
}
