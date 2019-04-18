package com.frisky.icebreaker.ui.pubs;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.assistant.UIHelper;

public class PubActivity extends AppCompatActivity {

    TextView mPubNameText;
    TextView mTagsText;
    TextView mRatingText;
    TextView mLocationText;
    AppBarLayout mAppBarLayout;

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
            mRatingText.setBackgroundResource(UIHelper.getInstance().getRatingBadgeBackground(pubRating));
            mAppBarLayout.setBackgroundResource(UIHelper.getInstance().getRatingBadgeColor(pubRating));
        }

        FloatingActionButton fab = findViewById(R.id.fab_chat_room);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Start Pub chat room here", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
