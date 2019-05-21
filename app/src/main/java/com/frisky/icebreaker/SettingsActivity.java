package com.frisky.icebreaker;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity implements UIActivity {

    ImageButton mBackButton;
    TextView mToolbarText;
    Button mLogoutButton;

    TextView mVersionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initUI();
    }

    @Override
    public void initUI() {
        mBackButton = findViewById(R.id.button_app_bar_left);
        mBackButton.setImageResource(R.drawable.round_arrow_back_24);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.super.onBackPressed();
            }
        });

        mToolbarText = findViewById(R.id.text_app_bar);
        mToolbarText.setText(R.string.settings);
        mToolbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.museosans700);
        mToolbarText.setTypeface(typeface);

        mLogoutButton = findViewById(R.id.button_logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent signOutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    signOutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(signOutIntent);
                    finish();
                }
            }
        });

        mVersionText = findViewById(R.id.text_version);
        String version = BuildConfig.VERSION_NAME;
        mVersionText.setText(version);
    }
}
