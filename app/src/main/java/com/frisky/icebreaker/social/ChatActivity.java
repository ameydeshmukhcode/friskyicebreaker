package com.frisky.icebreaker.social;

import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UIActivity;

public class ChatActivity extends AppCompatActivity implements UIActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUI();
    }

    public void initUI() {
        TextView mToolbarText;
        ImageButton mBackButton;
        ImageButton mMenuButton;
        ImageView mToolbarLogo;

        mToolbarLogo = findViewById(R.id.image_logo_text);
        mToolbarLogo.setVisibility(View.GONE);

        mToolbarText = findViewById(R.id.text_app_bar);
        if (getIntent().hasExtra("name")){
            String name = getIntent().getStringExtra("name");
            mToolbarText.setText(name);
        }
        mToolbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.museosans700);
        mToolbarText.setTypeface(typeface);

        mBackButton = findViewById(R.id.button_app_bar_left);
        mBackButton.setImageResource(R.drawable.ic_arrow_back);
        mBackButton.setOnClickListener(v -> ChatActivity.super.onBackPressed());

        mMenuButton = findViewById(R.id.button_app_bar_right);
        mMenuButton.setImageResource(R.drawable.ic_more_options);
    }
}
