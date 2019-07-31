package com.frisky.icebreaker;

import android.content.Intent;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity implements UIActivity {

    ImageButton mBackButton;
    ImageView mToolbarLogo;
    TextView mToolbarText;
    Button mLogoutButton;

    TextView mVersionText;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    @Override
    public void initUI() {
        mBackButton = findViewById(R.id.button_app_bar_left);
        mBackButton.setImageResource(R.drawable.ic_arrow_back);
        mBackButton.setOnClickListener(v -> SettingsActivity.super.onBackPressed());

        mToolbarLogo = findViewById(R.id.image_logo_text);
        mToolbarLogo.setVisibility(View.GONE);

        mToolbarText = findViewById(R.id.text_app_bar);
        mToolbarText.setText(R.string.settings);
        mToolbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.museosans700);
        mToolbarText.setTypeface(typeface);

        mLogoutButton = findViewById(R.id.button_logout);
        mLogoutButton.setOnClickListener(v -> {
            mAuth.signOut();

            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                Intent signOutIntent = new Intent(getApplicationContext(), SignInActivity.class);
                signOutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(signOutIntent);
                finish();
            }
        });

        mVersionText = findViewById(R.id.text_version);
        String version = BuildConfig.VERSION_NAME;
        mVersionText.setText(version);
    }
}
