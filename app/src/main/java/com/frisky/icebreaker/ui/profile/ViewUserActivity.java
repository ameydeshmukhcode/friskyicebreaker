package com.frisky.icebreaker.ui.profile;

import android.content.Intent;
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

        mProfileImagePager = findViewById(R.id.pager_profile_images);
        mProfileImagePager.setAdapter(mProfileImageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_image);
        tabLayout.setupWithViewPager(mProfileImagePager, true);
    }
}
