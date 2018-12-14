package com.frisky.icebreaker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private BottomNavigationView mBottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_nav_pubs:
                    mTextMessage.setText(R.string.bottom_nav_pubs);
                    return true;
                case R.id.bottom_nav_home:
                    mTextMessage.setText(R.string.bottom_nav_home);
                    return true;
                case R.id.bottom_nav_profile:
                    mTextMessage.setText(R.string.bottom_nav_profile);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTextMessage = findViewById(R.id.message);
        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_activity, menu);
        return true;
    }
}
