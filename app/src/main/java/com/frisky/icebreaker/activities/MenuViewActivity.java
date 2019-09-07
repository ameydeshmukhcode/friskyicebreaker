package com.frisky.icebreaker.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.MenuViewAdapter;

import java.util.ArrayList;

public class MenuViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_view);

        ImageButton nextButton = findViewById(R.id.button_next_image);
        ImageButton prevButton = findViewById(R.id.button_previous_image);

        ViewPager slideshow = findViewById(R.id.pager_menu_pages);
        ArrayList<Uri> mMenuList = getIntent().getParcelableArrayListExtra("menu_list");
        int currentPage = getIntent().getIntExtra("menu_page", 0);

        nextButton.setOnClickListener(v -> {
            slideshow.setCurrentItem(slideshow.getCurrentItem() + 1);
        });

        prevButton.setOnClickListener(v -> {
            slideshow.setCurrentItem(slideshow.getCurrentItem() - 1);
        });

        if (currentPage == 0) {
            prevButton.setVisibility(View.GONE);
        }
        else if (currentPage == (mMenuList.size() - 1)) {
            nextButton.setVisibility(View.GONE);
        }

        slideshow.setAdapter(new MenuViewAdapter(getApplicationContext(), mMenuList));
        slideshow.setCurrentItem(currentPage);

        slideshow.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    prevButton.setVisibility(View.GONE);
                }
                else if (position == (mMenuList.size() - 1)) {
                    nextButton.setVisibility(View.GONE);
                }
                else {
                    nextButton.setVisibility(View.VISIBLE);
                    prevButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
