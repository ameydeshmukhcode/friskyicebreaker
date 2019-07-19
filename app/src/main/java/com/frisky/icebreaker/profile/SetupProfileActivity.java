package com.frisky.icebreaker.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.HomeActivity;
import com.frisky.icebreaker.ui.assistant.RoundRectTransformation;
import com.frisky.icebreaker.ui.assistant.UIAssistant;
import com.frisky.icebreaker.ui.base.FormActivity;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.frisky.icebreaker.ui.components.dialogs.PickImageDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SetupProfileActivity extends AppCompatActivity implements FormActivity, UIActivity,
        PickImageDialog.OnImageUpdatedListener {

    ImageView mProfileImage;
    ImageButton mCancelButton;
    ImageButton mDoneButton;
    TextView mNameInput;
    TextView mBioInput;
    TextView mDateOfBirthInput;
    ProgressBar mProgressBar;
    ConstraintLayout mProgressLayout;
    Spinner mGenderSpinner;

    PickImageDialog pickImageDialog;

    FirebaseAuth mAuth;
    FirebaseStorage mStorage;
    StorageReference mStorageReference;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mFirestore = FirebaseFirestore.getInstance();

        initUI();
    }

    @Override
    public void initUI() {
        mNameInput = findViewById(R.id.input_name);
        mBioInput = findViewById(R.id.input_bio);
        mDateOfBirthInput = findViewById(R.id.input_date_of_birth);
        mProgressLayout = findViewById(R.id.layout_progress);
        mProfileImage = findViewById(R.id.image_profile);
        mCancelButton = findViewById(R.id.button_cancel);
        mDoneButton = findViewById(R.id.button_done);

        mGenderSpinner = findViewById(R.id.spinner_gender);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.genders, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mGenderSpinner.setAdapter(adapter);

        mProgressBar = findViewById(R.id.progress_upload);
        mProgressLayout.setVisibility(View.GONE);

        enableForm();
    }

    @Override
    public boolean validateForm() {
        String name = mNameInput.getText().toString();
        String bio = mBioInput.getText().toString();
        String dob = mDateOfBirthInput.getText().toString();

        if (name.matches("")) {
            Toast.makeText(SetupProfileActivity.this, "Enter name",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (dob.matches("")) {
            Toast.makeText(SetupProfileActivity.this, "Pick a Date of Birth",
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
        mGenderSpinner.setEnabled(false);
        mDateOfBirthInput.setEnabled(false);
    }

    @Override
    public void enableForm() {
        mNameInput.setEnabled(true);
        mBioInput.setEnabled(true);
        mGenderSpinner.setEnabled(true);
        mDateOfBirthInput.setEnabled(true);

        Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String dateFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
            mDateOfBirthInput.setText(sdf.format(calendar.getTime()));
        };

        mDateOfBirthInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            calendar.add(Calendar.YEAR, -18);
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        mDoneButton.setOnClickListener(v -> {
            if (validateForm()) {
                updateProfileData();
            }
        });

        mCancelButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            SetupProfileActivity.super.onBackPressed();
        });

        mProfileImage.setOnClickListener(view -> {
            pickImageDialog = new PickImageDialog();
            pickImageDialog.show(getSupportFragmentManager(), "pick image dialog");
        });
    }

    @Override
    public void imageUpdated(Uri bitmap) {
        Picasso.get().load(bitmap).transform(new RoundRectTransformation()).into(mProfileImage);
    }

    private void updateProfileData() {
        disableForm();
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user == null)
            return;

        final String userUid = user.getUid();

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", mNameInput.getText().toString());
        userDetails.put("bio", mBioInput.getText().toString());
        userDetails.put("gender", mGenderSpinner.getSelectedItem().toString());
        userDetails.put("date_of_birth", mDateOfBirthInput.getText().toString());
        userDetails.put("profile_setup_complete", true);

        mFirestore.collection("users")
                .document(userUid)
                .set(userDetails, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.i("User", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.e("Failed", e.getMessage(), e));

        final UploadTask uploadTask = mStorageReference.child("profile_images")
                .child(userUid).putFile(getImageUri());

        mProgressLayout.setVisibility(View.VISIBLE);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            int currentProgress = (int) progress;
            mProgressBar.setProgress(currentProgress);
        });

        uploadTask.addOnSuccessListener(taskSnapshot -> mStorageReference.child("profile_images").child(userUid)
                .getDownloadUrl().addOnSuccessListener(uri -> {
                    String name = mNameInput.getText().toString();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(uri)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.i("User", "User profile updated.");
                                    Intent launchHome = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(launchHome);
                                    finish();
                                }
                            });
                })).addOnFailureListener(e -> {
            enableForm();
            Log.e("Upload Error", e.getMessage());
        });
    }

    private Uri getImageUri() {
        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        File tmp = null;
        try {
            tmp = new File(getCacheDir() + "temporary.png");
            FileOutputStream ostream;
            ostream = new FileOutputStream(tmp);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
            return Uri.fromFile(UIAssistant.getInstance().compressImage(tmp, getApplicationContext()));
        }
        catch (IOException exp) {
            exp.printStackTrace();
        }

        return Uri.fromFile(tmp);
    }
}
