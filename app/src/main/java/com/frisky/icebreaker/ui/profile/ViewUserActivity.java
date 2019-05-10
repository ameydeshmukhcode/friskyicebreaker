package com.frisky.icebreaker.ui.profile;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;

public class ViewUserActivity extends AppCompatActivity {

    ViewPager mProfileImagePager;
    ProfileImageAdapter mProfileImageAdapter;
    TextView mUserNameText;
    ImageButton mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        mProfileImageAdapter = new ProfileImageAdapter(getApplicationContext());

        mUserNameText = findViewById(R.id.text_name);

        if (getIntent().hasExtra("name")){
            String name = getIntent().getStringExtra("name");
            mUserNameText.setText(name);
        }

        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUserActivity.super.onBackPressed();
            }
        });

        mProfileImagePager = findViewById(R.id.pager_profile_images);
        mProfileImagePager.setAdapter(mProfileImageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_image);
        tabLayout.setupWithViewPager(mProfileImagePager, true);

        FloatingActionButton fab = findViewById(R.id.fab_break_ice);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ICEBREAKER
                Snackbar.make(view, "Break Ice through here", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
