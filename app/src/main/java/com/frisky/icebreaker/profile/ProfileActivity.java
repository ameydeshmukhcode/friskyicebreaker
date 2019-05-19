package com.frisky.icebreaker.profile;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.LoginActivity;
import com.frisky.icebreaker.SettingsActivity;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements UIActivity {

    ImageButton mEditButton;
    ViewPager mProfileImagePager;
    ProfileImageAdapter mProfileImageAdapter;
    ImageButton mBackButton;
    ImageButton mSettingsButton;
    TextView mNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initUI();
    }

    @Override
    public void initUI() {
        mEditButton = findViewById(R.id.button_edit);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                startActivity(editProfile);
            }
        });

        mProfileImageAdapter = new ProfileImageAdapter(getApplicationContext());

        mProfileImagePager = findViewById(R.id.pager_profile_images);
        mProfileImagePager.setAdapter(mProfileImageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_image);
        tabLayout.setupWithViewPager(mProfileImagePager, true);

        mNameText = findViewById(R.id.text_name);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            mNameText.setText(user.getDisplayName());
    }
}
