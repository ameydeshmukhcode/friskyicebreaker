package com.frisky.icebreaker.ui.profile;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.frisky.icebreaker.R;

public class ProfileActivity extends AppCompatActivity {

    ImageButton mEditButton;
    ViewPager mProfileImagePager;
    ProfileImageAdapter mProfileImageAdapter;

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

        mProfileImageAdapter = new ProfileImageAdapter(getApplicationContext());

        mProfileImagePager = findViewById(R.id.pager_profile_images);
        mProfileImagePager.setAdapter(mProfileImageAdapter);
    }
}
