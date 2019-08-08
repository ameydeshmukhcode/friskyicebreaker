package com.frisky.icebreaker.profile;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewUserActivity extends AppCompatActivity implements UIActivity {

    ViewPager mProfileImagePager;
    ProfileImageAdapter mProfileImageAdapter;
    TextView mUserNameText;
    ImageButton mBackButton;
    TextView mBioText;

    String mUserId;

    FirebaseStorage mStorage;
    StorageReference mStorageReference;
    FirebaseFirestore mFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        initUI();
    }

    @Override
    public void initUI() {
        mProfileImageAdapter = new ProfileImageAdapter(getApplicationContext());

        mUserNameText = findViewById(R.id.text_name);

        if (getIntent().hasExtra("name")){
            String name = getIntent().getStringExtra("name");
            mUserNameText.setText(name);
        }

        if (getIntent().hasExtra("id")) {
            String id = getIntent().getStringExtra("id");
            this.mUserId = id;
            Log.d(getString(R.string.tag_debug), "User " + id);
        }

        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> ViewUserActivity.super.onBackPressed());

        mProfileImagePager = findViewById(R.id.pager_profile_images);
        mProfileImagePager.setAdapter(mProfileImageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_image);
        tabLayout.setupWithViewPager(mProfileImagePager, true);

        FloatingActionButton fab = findViewById(R.id.fab_break_ice);
        fab.setOnClickListener(view -> {
            //TODO ICEBREAKER
            Snackbar.make(view, "Break Ice through here", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });

        mBioText = findViewById(R.id.text_bio);
        DocumentReference docRef = mFirebaseFirestore.collection("users").document(mUserId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null)
                    return;
                if (document.exists()) {
                    mBioText.setText(document.getString("bio"));
                    Log.d(getString(R.string.tag_debug), "DocumentSnapshot data: " + document.getData());
                }
                else {
                    Log.e("Doesn't exist", "No such document");
                }
            }
            else {
                Log.e("Task", "failed with ", task.getException());
            }
        });

        getProfileImage();
    }

    private void getProfileImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileImageRef = storageReference.child("profile_images").child(mUserId);

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            mProfileImageAdapter.addToImageList(uri);
            mProfileImageAdapter.notifyDataSetChanged();
            Log.d(getString(R.string.tag_debug), "Image " + uri.toString());
        }).addOnFailureListener(e -> Log.e("Uri Download Failed", e.getMessage()));
    }
}
