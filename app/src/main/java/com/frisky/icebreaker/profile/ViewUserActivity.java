package com.frisky.icebreaker.profile;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    String UID;

    FirebaseStorage mStorage;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

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
            this.UID = id;
            Log.i("ID", id);
        }

        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUserActivity.super.onBackPressed();
            }
        });

        mProfileImagePager = findViewById(R.id.pager_profile_images);
        mProfileImagePager.setAdapter(mProfileImageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_image);
        tabLayout.setupWithViewPager(mProfileImagePager, true);

        FloatingActionButton fab = findViewById(R.id.fab_break_ice);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ICEBREAKER
                Snackbar.make(view, "Break Ice through here", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mBioText = findViewById(R.id.text_bio);
        DocumentReference docRef = firebaseFirestore.collection("users").document(UID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document == null)
                        return;
                    if (document.exists()) {
                        mBioText.setText(document.get("bio").toString());
                        Log.d("Exists", "DocumentSnapshot data: " + document.getData());
                    }
                    else {
                        Log.d("Doesn't exist", "No such document");
                    }
                }
                else {
                    Log.d("Task", "failed with ", task.getException());
                }
            }
        });

        getProfileImage();
    }

    private void getProfileImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileImageRef = storageReference.child("profile_images").child(UID);

        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                mProfileImageAdapter.addToImageList(uri);
                mProfileImageAdapter.notifyDataSetChanged();
                Log.d("Image Uri downloaded", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Uri Download Failed", e.getMessage());
            }
        });
    }
}
