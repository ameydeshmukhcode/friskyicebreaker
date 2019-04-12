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

    private BottomNavigationView mBottomNavigationView;
    private ImageButton mSocialButton;
    private ImageButton mScanQRCodeButton;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_nav_home:
                    loadFragment(new PubViewFragment());
                    return true;
                case R.id.bottom_nav_icebreaker:
                    loadFragment(new UsersListFragment());
                    return true;
                case R.id.bottom_nav_profile:
                    loadFragment(new ProfileFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();

        loadFragment(new PubViewFragment());

        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
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
