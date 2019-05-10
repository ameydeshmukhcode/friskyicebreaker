package com.frisky.icebreaker.ui.social;

import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;

public class ChatActivity extends AppCompatActivity {

    private TextView mToolbarText;
    private ImageButton mBackButton;
    private ImageButton mMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initUI();
    }

    private void initUI() {
        mToolbarText = findViewById(R.id.text_app_bar);
        if (getIntent().hasExtra("name")){
            String name = getIntent().getStringExtra("name");
            mToolbarText.setText(name);
        }
        mToolbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.museosans700);
        mToolbarText.setTypeface(typeface);

        mBackButton = findViewById(R.id.button_app_bar_left);
        mBackButton.setImageResource(R.drawable.round_arrow_back_24);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.super.onBackPressed();
            }
        });

        mMenuButton = findViewById(R.id.button_app_bar_right);
        mMenuButton.setImageResource(R.drawable.round_more_vert_24);
    }
}
