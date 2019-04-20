package com.frisky.icebreaker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.profile.ProfileActivity;
import com.frisky.icebreaker.ui.pubs.PubViewFragment;
import com.frisky.icebreaker.ui.social.SocialFragment;
import com.frisky.icebreaker.ui.social.UsersListFragment;

public class HomeActivity extends AppCompatActivity {

    private ImageButton mSocialButton;
    private ImageButton mScanQRCodeButton;
    private ImageButton mBottomNavHomeButton;
    private ImageButton mBottomNavProfileButton;
    private ImageButton m2ND;
    private ImageButton m4TH;
    private ImageButton mIceBreakerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();

        loadFragment(new PubViewFragment());
    }

    private void initUI() {
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

        m2ND = findViewById(R.id.button_nav_centre_left);
        m2ND.setImageResource(R.drawable.placeholder_24);

        m4TH = findViewById(R.id.button_nav_centre_right);
        m4TH.setImageResource(R.drawable.placeholder_24);

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
                loadFragment(new UsersListFragment());
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
