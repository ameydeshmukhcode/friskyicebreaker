package com.frisky.icebreaker.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        Button nextButton = findViewById(R.id.button_next_image);
        Button prevButton = findViewById(R.id.button_previous_image);
        Button mCancelButton = findViewById(R.id.button_cancel);
        TextView textCurrent = findViewById(R.id.text_current_page);
        TextView totalPages = findViewById(R.id.text_total_pages);

        int currentPage = getIntent().getIntExtra("menu_page", 0);
        ViewPager slideshow = findViewById(R.id.pager_menu_pages);
        ArrayList<Uri> mMenuList = getIntent().getParcelableArrayListExtra("menu_list");

        mCancelButton.setOnClickListener(v -> onBackPressed());

        nextButton.setOnClickListener(v -> slideshow.setCurrentItem(slideshow.getCurrentItem() + 1));

        prevButton.setOnClickListener(v -> slideshow.setCurrentItem(slideshow.getCurrentItem() - 1));

        if (currentPage == 0) {
            prevButton.setVisibility(View.INVISIBLE);
        } else if (currentPage == (mMenuList.size() - 1)) {
            nextButton.setVisibility(View.INVISIBLE);
        }

        slideshow.setAdapter(new MenuViewAdapter(getApplicationContext(), mMenuList));
        slideshow.setCurrentItem(currentPage);

        textCurrent.setText(String.valueOf(currentPage + 1));
        totalPages.setText(String.valueOf(mMenuList.size()));

        slideshow.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                textCurrent.setText(String.valueOf(position + 1));
                if (position == 0) {
                    prevButton.setVisibility(View.INVISIBLE);
                } else if (position == (mMenuList.size() - 1)) {
                    nextButton.setVisibility(View.INVISIBLE);
                } else {
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
