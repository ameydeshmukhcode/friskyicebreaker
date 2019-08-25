package com.frisky.icebreaker.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.MenuImagesAdapter;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.google.android.material.appbar.AppBarLayout;

import static com.frisky.icebreaker.ui.assistant.UIAssistant.getRatingBadgeColor;

public class RestaurantActivity extends AppCompatActivity implements UIActivity {

    TextView mPubNameText;
    TextView mTagsText;
    TextView mRatingText;
    TextView mLocationText;
    AppBarLayout mAppBarLayout;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        initUI();
    }

    public void initUI() {
        mPubNameText = findViewById(R.id.text_pub_name);
        mTagsText = findViewById(R.id.text_cuisine);
        mRatingText = findViewById(R.id.text_rating);
        mLocationText = findViewById(R.id.text_location);
        mAppBarLayout = findViewById(R.id.app_bar_container);
        mToolbar = findViewById(R.id.app_bar_pub);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (getIntent().hasExtra("name")){
            mPubNameText.setText(getIntent().getStringExtra("name"));
        }

        if (getIntent().hasExtra("tags")) {
            mTagsText.setText(getIntent().getStringExtra("tags"));
        }

        if (getIntent().hasExtra("location")) {
            mLocationText.setText(getIntent().getStringExtra("location"));
        }

        if (getIntent().hasExtra("rating")) {
            String pubRatingText = getIntent().getStringExtra("rating");
            double pubRating = Double.parseDouble(pubRatingText);
            mRatingText.setText(getIntent().getStringExtra("rating"));
            mRatingText.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext()
                    .getColor(getRatingBadgeColor(pubRating))));
            mAppBarLayout.setBackgroundResource(getRatingBadgeColor(pubRating));
        }

        if (getIntent().hasExtra("id")) {
            String id = getIntent().getStringExtra("id");
            Log.d(getString(R.string.tag_debug), "restaurant " + id);
        }

        RecyclerView mRecyclerMenuView;
        mRecyclerMenuView = findViewById(R.id.recycler_view_menu);

        LinearLayoutManager mMenuViewLayoutManager;
        // use a linear layout manager
        mMenuViewLayoutManager = new LinearLayoutManager(this);
        mMenuViewLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerMenuView.setLayoutManager(mMenuViewLayoutManager);

        // specify an adapter (see also next example)
        mRecyclerMenuView.setAdapter(new MenuImagesAdapter());
    }
}
