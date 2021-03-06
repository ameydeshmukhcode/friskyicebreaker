package com.frisky.icebreaker.activities;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.MenuImagesAdapter;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.frisky.icebreaker.ui.assistant.UIAssistant.getRatingBadgeColor;

public class RestaurantActivity extends AppCompatActivity implements UIActivity {

    TextView mPubNameText;
    TextView mTagsText;
    TextView mRatingText;
    TextView mLocationText;
    ImageView mImageHeader;
    Toolbar mToolbar;

    ArrayList<Uri> mMenuList = new ArrayList<>();

    MenuImagesAdapter menuImagesAdapter;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        initUI();
    }

    public void initUI() {
        mPubNameText = findViewById(R.id.text_restaurant_name);
        mTagsText = findViewById(R.id.text_cuisine);
        mRatingText = findViewById(R.id.text_rating);
        mLocationText = findViewById(R.id.text_location);
        mImageHeader = findViewById(R.id.image_restaurant_header);
        mToolbar = findViewById(R.id.toolbar_restaurant);
        shimmerFrameLayout = findViewById(R.id.shimmer_menu_list);
        shimmerFrameLayout.startShimmer();

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

        if (getIntent().hasExtra("image")) {
            Picasso.get().load(Uri.parse(getIntent().getStringExtra("image"))).into(mImageHeader);
        }

        if (getIntent().hasExtra("rating")) {
            String pubRatingText = getIntent().getStringExtra("rating");
            double pubRating = Double.parseDouble(pubRatingText);
            mRatingText.setText(getIntent().getStringExtra("rating"));
            mRatingText.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext()
                    .getColor(getRatingBadgeColor(pubRating))));
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

        menuImagesAdapter = new MenuImagesAdapter(getApplicationContext(), mMenuList);
        // specify an adapter (see also next example)
        mRecyclerMenuView.setAdapter(menuImagesAdapter);

        if (getIntent().hasExtra("id")) {
            getMenuImages(getIntent().getStringExtra("id"));
        }
    }

    private void getMenuImages(String id) {
        StorageReference reference = FirebaseStorage.getInstance().getReference();

        reference.child("restaurants/" + id + "/menu")
                .listAll()
                .addOnCompleteListener(task -> {
                    for (StorageReference ref: task.getResult().getItems()) {
                        ref.getDownloadUrl().addOnCompleteListener(task1 -> {
                            mMenuList.add(task1.getResult());
                            menuImagesAdapter.notifyDataSetChanged();
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        });
                    }
                });
    }
}
