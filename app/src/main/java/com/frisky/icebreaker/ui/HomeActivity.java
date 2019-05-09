package com.frisky.icebreaker.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.profile.ProfileActivity;
import com.frisky.icebreaker.ui.pubs.PubViewFragment;
import com.frisky.icebreaker.ui.social.IceBreakerFragment;
import com.frisky.icebreaker.ui.social.SocialFragment;

public class HomeActivity extends AppCompatActivity {

    private ImageButton mSocialButton;
    private ImageButton mScanQRCodeButton;
    private ImageButton mBottomNavHomeButton;
    private ImageButton mBottomNavProfileButton;
    private ImageButton mBottomNavOrderButton;
    private ImageButton mBottomNavNotificationButton;
    private ImageButton mIceBreakerButton;
    private TextView mToolbarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();

        loadFragment(new PubViewFragment());
    }

    private void initUI() {
        mToolbarText = findViewById(R.id.toolbar_text);
        mToolbarText.setText(R.string.app_name);
        mToolbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.ktfroadstar);
        mToolbarText.setTypeface(typeface);

        mSocialButton = findViewById(R.id.button_toolbar_right);
        mSocialButton.setImageResource(R.drawable.round_chat_24);
        mSocialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SocialFragment());
            }
        });

        mScanQRCodeButton = findViewById(R.id.button_toolbar_left);
        mScanQRCodeButton.setImageResource(R.drawable.round_camera_24);

        mBottomNavHomeButton = findViewById(R.id.button_nav_left);
        mBottomNavHomeButton.setImageResource(R.drawable.round_home_24);
        mBottomNavHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PubViewFragment());
            }
        });

        mBottomNavOrderButton = findViewById(R.id.button_nav_centre_left);
        mBottomNavOrderButton.setImageResource(R.drawable.round_receipt_24);

        mBottomNavNotificationButton = findViewById(R.id.button_nav_centre_right);
        mBottomNavNotificationButton.setImageResource(R.drawable.round_notifications_none_24);

        mBottomNavProfileButton = findViewById(R.id.button_nav_right);
        mBottomNavProfileButton.setImageResource(R.drawable.round_person_24);
        mBottomNavProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startProfileActivity = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(startProfileActivity);
            }
        });

        mIceBreakerButton = findViewById(R.id.button_icebreaker);
        mIceBreakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new IceBreakerFragment());
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_activity_fragment, fragment)
                    .commit();

            return true;
        }
        return false;
    }
}
