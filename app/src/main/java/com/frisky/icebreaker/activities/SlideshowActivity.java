package com.frisky.icebreaker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.SlideshowAdapter;

public class SlideshowActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        if (sharedPreferences.contains("slideshow_complete")) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        else {
            setContentView(R.layout.activity_slideshow);

            ViewPager slideshow = findViewById(R.id.pager_slideshow);
            slideshow.setAdapter(new SlideshowAdapter(getApplicationContext()));
        }
    }
}
