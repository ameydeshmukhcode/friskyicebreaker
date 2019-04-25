package com.frisky.icebreaker.ui.profile;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;

public class EditProfileActivity extends AppCompatActivity {

    private TextView mToolbarText;
    private ImageButton mBackButton;
    private ImageButton mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initUI();
    }

    private void initUI() {
        mToolbarText = findViewById(R.id.toolbar_text);
        mToolbarText.setText(R.string.edit_profile);
        mToolbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.museosans700);
        mToolbarText.setTypeface(typeface);

        mBackButton = findViewById(R.id.button_toolbar_left);
        mBackButton.setImageResource(R.drawable.round_arrow_back_24);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileActivity.super.onBackPressed();
            }
        });

        mDoneButton = findViewById(R.id.button_toolbar_right);
        mDoneButton.setImageResource(R.drawable.round_done_24);
    }
}
