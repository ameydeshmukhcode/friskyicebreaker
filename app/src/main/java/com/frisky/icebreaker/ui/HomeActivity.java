package com.frisky.icebreaker.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.profile.ProfileFragment;
import com.frisky.icebreaker.ui.pubview.PubViewFragment;
import com.frisky.icebreaker.ui.social.SocialFragment;
import com.frisky.icebreaker.ui.social.UsersListFragment;

public class HomeActivity extends AppCompatActivity {

    private ImageButton mSocialButton;
    private ImageButton mScanQRCodeButton;
    private ImageButton mBottomNavHomeButton;
    private ImageButton mBottomNavProfileButton;
    private ImageButton m2ND;
    private ImageButton m4TH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();

        loadFragment(new PubViewFragment());
    }

    private void initUI() {
        mSocialButton = findViewById(R.id.button_toolbar_right);
        mSocialButton.setBackgroundResource(R.drawable.round_chat_24);
        mSocialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SocialFragment());
            }
        });

        mScanQRCodeButton = findViewById(R.id.button_toolbar_left);
        mScanQRCodeButton.setBackgroundResource(R.drawable.round_camera_24);

        mBottomNavHomeButton = findViewById(R.id.button_nav_left);
        mBottomNavHomeButton.setBackgroundResource(R.drawable.round_home_24);
        mBottomNavHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PubViewFragment());
            }
        });

        m2ND = findViewById(R.id.button_nav_centre_left);
        m2ND.setBackgroundResource(R.drawable.placeholder_24);

        m4TH = findViewById(R.id.button_nav_centre_right);
        m4TH.setBackgroundResource(R.drawable.placeholder_24);

        mBottomNavProfileButton = findViewById(R.id.button_nav_right);
        mBottomNavProfileButton.setBackgroundResource(R.drawable.round_person_24);
        mBottomNavProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProfileFragment());
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
