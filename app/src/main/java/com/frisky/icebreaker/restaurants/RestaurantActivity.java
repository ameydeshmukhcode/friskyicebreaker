package com.frisky.icebreaker.restaurants;

import android.content.res.ColorStateList;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.assistant.UIAssistant;
import com.frisky.icebreaker.ui.base.UIActivity;

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
                    .getColor(UIAssistant.getInstance().getRatingBadgeColor(pubRating))));
            mAppBarLayout.setBackgroundResource(UIAssistant.getInstance().getRatingBadgeColor(pubRating));
        }

        if (getIntent().hasExtra("id")) {
            String id = getIntent().getStringExtra("id");
            Log.d(getString(R.string.tag_debug), "restaurant " + id);
        }

        FloatingActionButton fab = findViewById(R.id.fab_chat_room);
        fab.setOnClickListener(view -> {
            //TODO PUB CHAT ROOM
            Snackbar.make(view, "Start Restaurant chat room here", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });
    }
}
