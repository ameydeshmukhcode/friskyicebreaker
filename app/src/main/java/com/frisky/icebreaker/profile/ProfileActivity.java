package com.frisky.icebreaker.profile;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.SettingsActivity;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity implements UIActivity {

    ImageButton mEditButton;
    ViewPager mProfileImagePager;
    ProfileImageAdapter mProfileImageAdapter;
    ImageButton mBackButton;
    ImageButton mSettingsButton;
    TextView mNameText;
    TextView mBioText;

    FirebaseAuth mAuth;
    FirebaseStorage mStorage;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        initUI();
    }

    @Override
    public void initUI() {
        mEditButton = findViewById(R.id.button_edit);
        mEditButton.setOnClickListener(v -> {
            Intent editProfile = new Intent(getApplicationContext(), EditProfileActivity.class);
            editProfile.putExtra("bio", mBioText.getText().toString());
            startActivity(editProfile);
        });

        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> ProfileActivity.super.onBackPressed());

        mSettingsButton = findViewById(R.id.button_settings);
        mSettingsButton.setOnClickListener(v -> {
            Intent editProfile = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(editProfile);
        });

        mProfileImageAdapter = new ProfileImageAdapter(getApplicationContext());

        mProfileImagePager = findViewById(R.id.pager_profile_images);
        mProfileImagePager.setAdapter(mProfileImageAdapter);
        mProfileImagePager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        TabLayout tabLayout = findViewById(R.id.tab_image);
        tabLayout.setupWithViewPager(mProfileImagePager, true);

        mNameText = findViewById(R.id.text_name);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
            mNameText.setText(user.getDisplayName());

        mBioText = findViewById(R.id.text_bio);
        DocumentReference docRef = firebaseFirestore.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null)
                    return;
                if (document.exists()) {
                    mBioText.setText(document.getString("bio"));
                    Log.i("Exists", "DocumentSnapshot data: " + document.getData());
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
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null)
            return;

        StorageReference profileImageRef = storageReference.child("profile_images").child(mAuth.getCurrentUser().getUid());

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            mProfileImageAdapter.addToImageList(uri);
            mProfileImageAdapter.notifyDataSetChanged();
            Log.i("Image Uri Downloaded", uri.toString());
        }).addOnFailureListener(e -> Log.e("Uri Download Failed", e.getMessage()));
    }
}
