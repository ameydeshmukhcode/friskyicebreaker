package com.frisky.icebreaker.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.profile.ProfileFragment;
import com.frisky.icebreaker.ui.pubview.PubViewFragment;
import com.frisky.icebreaker.ui.social.SocialFragment;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolbarMain;
    private BottomNavigationView mBottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_nav_home:
                    loadFragment(new PubViewFragment());
                    changeTitle(R.string.app_name);
                    return true;
                case R.id.bottom_nav_profile:
                    loadFragment(new ProfileFragment());
                    changeTitle(R.string.profile);
                    return true;
                case R.id.bottom_nav_icebreaker:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbarMain = findViewById(R.id.home_activity_toolbar);
        changeTitle(R.string.app_name);

        mToolbarMain.inflateMenu(R.menu.menu_home_activity);

        mToolbarMain.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch(item.getItemId()){
                            case R.id.home_menu_scan_qr:
                                //TODO: Start QR Scanning Activity from here
                                return true;
                            case R.id.home_menu_social:
                                loadFragment(new SocialFragment());
                                changeTitle(R.string.social);
                                return true;
                        }
                        return false;
                    }
                });

        loadFragment(new PubViewFragment());

        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
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

    private void changeTitle(int stringResource) {
        mToolbarMain.setTitle(stringResource);
    }
}
