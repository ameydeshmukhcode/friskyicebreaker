package com.frisky.icebreaker.ui.pubs;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.assistant.UIAssistant;

public class PubActivity extends AppCompatActivity {

    TextView mPubNameText;
    TextView mTagsText;
    TextView mRatingText;
    TextView mLocationText;
    AppBarLayout mAppBarLayout;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub);

        initUI();
    }

    private void initUI() {
        mPubNameText = findViewById(R.id.text_pub_name);
        mTagsText = findViewById(R.id.text_tags);
        mRatingText = findViewById(R.id.text_rating);
        mLocationText = findViewById(R.id.text_location);
        mAppBarLayout = findViewById(R.id.app_bar);
        mToolbar = findViewById(R.id.toolbar);

        mToolbar.setNavigationIcon(R.drawable.round_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
            Double pubRating = Double.parseDouble(pubRatingText);
            mRatingText.setText(getIntent().getStringExtra("rating"));
            mRatingText.setBackgroundResource(UIAssistant.getInstance().getRatingBadgeBackground(pubRating));
            mAppBarLayout.setBackgroundResource(UIAssistant.getInstance().getRatingBadgeColor(pubRating));
        }

        FloatingActionButton fab = findViewById(R.id.fab_chat_room);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO PUB CHAT ROOM
                Snackbar.make(view, "Start Pub chat room here", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
