package com.frisky.icebreaker.profile;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.frisky.icebreaker.ui.components.dialogs.PickImageDialog;

public class EditProfileActivity extends AppCompatActivity implements UIActivity, PickImageDialog.OnImageUpdatedListener {

    TextView mToolbarText;
    EditText mBioInput;
    ImageButton mBackButton;
    ImageButton mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initUI();
    }

    public void initUI() {
        mToolbarText = findViewById(R.id.text_app_bar);
        mToolbarText.setText(R.string.edit_profile);
        mToolbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.museosans700);
        mToolbarText.setTypeface(typeface);

        mBackButton = findViewById(R.id.button_app_bar_left);
        mBackButton.setImageResource(R.drawable.round_arrow_back_24);
        mBackButton.setOnClickListener(v -> EditProfileActivity.super.onBackPressed());

        RecyclerView mRecyclerImageGridView;
        RecyclerView.LayoutManager mEditImagesGridLayoutManager;
        RecyclerView.Adapter mImageGridAdapter;

        mRecyclerImageGridView = findViewById(R.id.recycler_view);

        mRecyclerImageGridView.setHasFixedSize(true);
        mRecyclerImageGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // use a grid layout manager
        mEditImagesGridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mRecyclerImageGridView.setLayoutManager(mEditImagesGridLayoutManager);

        // specify an adapter (see also next example)
        mImageGridAdapter = new EditImagesAdapter(this);
        mRecyclerImageGridView.setAdapter(mImageGridAdapter);

        mDoneButton = findViewById(R.id.button_app_bar_right);
        mDoneButton.setImageResource(R.drawable.round_done_24);

        mBioInput = findViewById(R.id.input_bio);

        if (getIntent().hasExtra("bio")){
            mBioInput.setHint(getIntent().getStringExtra("bio"));
        }
    }

    @Override
    public void imageUpdated(Uri bitmap) {

    }
}
