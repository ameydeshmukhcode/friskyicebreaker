package com.frisky.icebreaker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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

            Button skipButton = findViewById(R.id.button_skip);
            Button nextButton = findViewById(R.id.button_next);

            ViewPager slideshow = findViewById(R.id.pager_slideshow);
            slideshow.setAdapter(new SlideshowAdapter(getApplicationContext()));

            skipButton.setOnClickListener(v -> {
                sharedPreferences.edit().putBoolean("slideshow_complete", true).apply();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
            });

            slideshow.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 2) {
                        nextButton.setText("Continue");
                        nextButton.setOnClickListener(v -> {
                            sharedPreferences.edit().putBoolean("slideshow_complete", true).apply();
                            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                            finish();
                        });
                    }
                    else {
                        nextButton.setText("Next");
                        nextButton.setOnClickListener(v -> {
                            int currentItem = slideshow.getCurrentItem();
                            slideshow.setCurrentItem(currentItem + 1);
                        });
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }
}
