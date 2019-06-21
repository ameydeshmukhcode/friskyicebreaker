package com.frisky.icebreaker.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.HomeActivity;
import com.frisky.icebreaker.ui.assistant.UIAssistant;
import com.frisky.icebreaker.ui.base.FormActivity;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.frisky.icebreaker.ui.components.dialogs.PickImageDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SetupProfileActivity extends AppCompatActivity implements FormActivity, UIActivity,
        PickImageDialog.OnImageUpdatedListener {

    ImageView mProfileImage;
    ImageButton mCancelButton;
    ImageButton mDoneButton;
    TextView mNameInput;
    TextView mBioInput;
    ProgressBar mProgressBar;
    ConstraintLayout mProgressLayout;

    PickImageDialog pickImageDialog;

    FirebaseAuth mAuth;
    FirebaseStorage mStorage;
    StorageReference storageReference;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        mFirestore = FirebaseFirestore.getInstance();

        initUI();
    }

    @Override
    public void initUI() {
        mNameInput = findViewById(R.id.input_name);
        mBioInput = findViewById(R.id.input_bio);
        mProgressLayout = findViewById(R.id.layout_progress);
        mProfileImage = findViewById(R.id.image_profile);
        mCancelButton = findViewById(R.id.button_cancel);
        mDoneButton = findViewById(R.id.button_done);
        mProgressBar = findViewById(R.id.progress_upload);
        mProgressLayout.setVisibility(View.GONE);

        enableForm();
    }

    @Override
    public boolean validateForm() {
        String name = mNameInput.getText().toString();
        String bio = mBioInput.getText().toString();

        if (name.matches("")) {
            Toast.makeText(SetupProfileActivity.this, "Enter name",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (bio.matches("")) {
            Toast.makeText(SetupProfileActivity.this, "Enter bio",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void disableForm() {
        mProfileImage.setOnClickListener(null);
        mDoneButton.setOnClickListener(null);
        mCancelButton.setOnClickListener(null);
        mNameInput.setEnabled(false);
        mBioInput.setEnabled(false);
    }

    @Override
    public void enableForm() {
        mNameInput.setEnabled(true);
        mBioInput.setEnabled(true);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    updateProfileData();
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupProfileActivity.super.onBackPressed();
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageDialog = new PickImageDialog();
                pickImageDialog.show(getSupportFragmentManager(), "pick image dialog");
            }
        });
    }

    @Override
    public void imageUpdated(Uri bitmap) {
        Picasso.get().load(bitmap).into(mProfileImage);
    }

    private void updateProfileData() {
        disableForm();
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user == null)
            return;

        final String userUid = user.getUid();

        Map<String, Object> firestoreUser = new HashMap<>();
        firestoreUser.put("name", mNameInput.getText().toString());
        firestoreUser.put("bio", mBioInput.getText().toString());

        mFirestore.collection("users")
                .document(userUid)
                .set(firestoreUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("User", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Failed", e.getMessage(), e);
                    }
                });

        final UploadTask uploadTask = storageReference.child("profile_images")
                .child(userUid).putBytes(getImageData());

        mProgressLayout.setVisibility(View.VISIBLE);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                int currentProgress = (int) progress;
                mProgressBar.setProgress(currentProgress);
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                storageReference.child("profile_images").child(userUid)
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String name = mNameInput.getText().toString();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.i("User", "User profile updated.");
                                            Intent launchHome = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(launchHome);
                                            finish();
                                        }
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                enableForm();
                Log.e("Upload Error", e.getMessage());
            }
        });
    }

    private byte[] getImageData() {
        mProfileImage.setDrawingCacheEnabled(true);
        mProfileImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
