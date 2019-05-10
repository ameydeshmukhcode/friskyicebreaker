package com.frisky.icebreaker.ui.profile;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.SettingsActivity;

public class ProfileActivity extends AppCompatActivity {

    ImageButton mEditButton;
    ViewPager mProfileImagePager;
    ProfileImageAdapter mProfileImageAdapter;
    ImageButton mBackButton;
    ImageButton mSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mEditButton = findViewById(R.id.button_edit);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Start edit profile activity here
                Intent editProfile = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(editProfile);
            }
        });

        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();
            }
        });

        mSettingsButton = findViewById(R.id.button_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfile = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(editProfile);            }
        });


        mProfileImageAdapter = new ProfileImageAdapter(getApplicationContext());

        mProfileImagePager = findViewById(R.id.pager_profile_images);
        mProfileImagePager.setAdapter(mProfileImageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_image);
        tabLayout.setupWithViewPager(mProfileImagePager, true);
    }
}
