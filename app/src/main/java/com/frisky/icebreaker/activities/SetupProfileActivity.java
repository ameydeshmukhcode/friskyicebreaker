package com.frisky.icebreaker.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.interfaces.FormActivity;
import com.frisky.icebreaker.ui.assistant.RoundRectTransformation;
import com.frisky.icebreaker.ui.components.dialogs.PickImageDialog;
import com.frisky.icebreaker.ui.components.dialogs.ProgressDialog;
import com.google.android.material.textfield.TextInputLayout;
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

import static com.frisky.icebreaker.ui.assistant.UIAssistant.compressImage;

public class SetupProfileActivity extends AppCompatActivity implements FormActivity,
        PickImageDialog.OnImageUpdatedListener {

    ImageView mProfileImage;
    Button mCancelButton;
    Button mDoneButton;
    TextView mNameInput;
    TextView mBioInput;
    TextView mDateOfBirthInput;
    TextInputLayout dobLayout;
    TextInputLayout genderLayout;
    AutoCompleteTextView mGenderPicker;
    View dobView;

    PickImageDialog pickImageDialog;

    FirebaseUser mUser;
    FirebaseStorage mStorage;
    StorageReference mStorageReference;
    FirebaseFirestore mFirestore;
    SharedPreferences sharedPreferences;

    boolean imageNotSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mFirestore = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        initUI();
    }

    @Override
    public void initUI() {
        mNameInput = findViewById(R.id.input_name);
        mBioInput = findViewById(R.id.input_bio);
        mDateOfBirthInput = findViewById(R.id.input_date_of_birth);
        mProfileImage = findViewById(R.id.image_profile);
        mCancelButton = findViewById(R.id.button_cancel);
        mDoneButton = findViewById(R.id.button_done);

        dobView = findViewById(R.id.dobView);
        dobLayout = findViewById(R.id.dobInput);
        genderLayout = findViewById(R.id.genderLayout);

        mGenderPicker = findViewById(R.id.dropdown_gender);

        String[] GENDERS = new String[] {"Male", "Female", "Other"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getBaseContext(),
                        R.layout.dropdown_text,
                        GENDERS);

        mGenderPicker.setAdapter(adapter);

        if (getIntent().hasExtra("edit_mode")) {
            genderLayout.setVisibility(View.GONE);
            dobLayout.setVisibility(View.GONE);

            Picasso.get().load(sharedPreferences.getString("u_image", ""))
                    .transform(new RoundRectTransformation()).into(mProfileImage);
        }

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

        if (bio.length() > 50) {
            Toast.makeText(SetupProfileActivity.this, "Bio too long",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mGenderPicker.getText().toString().matches("")) {
            Toast.makeText(SetupProfileActivity.this, "Gender not selected",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (imageNotSelected) {
            Toast.makeText(SetupProfileActivity.this, "Pick a profile photo",
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
        mGenderPicker.setEnabled(false);
        mDateOfBirthInput.setEnabled(false);
    }

    @Override
    public void enableForm() {
        mNameInput.setEnabled(true);
        mBioInput.setEnabled(true);
        mGenderPicker.setEnabled(true);
        mDateOfBirthInput.setEnabled(true);

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String dateFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
            mDateOfBirthInput.setText(sdf.format(calendar.getTime()));
        };

        dobView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            calendar.add(Calendar.YEAR, -18);
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        mDoneButton.setOnClickListener(v -> {
            if (getIntent().hasExtra("edit_mode")) {
                updateEditedDetails();
            } else if (validateForm()) {
                updateProfileData();
            }
        });

        mCancelButton.setOnClickListener(v -> onBackPressed());

        mProfileImage.setOnClickListener(view -> {
            pickImageDialog = new PickImageDialog();
            pickImageDialog.show(getSupportFragmentManager(), "pick image dialog");
        });
    }

    @Override
    public void imageUpdated(Uri bitmap) {
        Picasso.get().load(bitmap).transform(new RoundRectTransformation()).into(mProfileImage);
        imageNotSelected = false;
    }

    private void updateEditedDetails() {
        disableForm();

        boolean nameEdited = false;
        boolean bioEdited = false;

        String name = mNameInput.getText().toString();
        String bio = mBioInput.getText().toString();

        if (!name.matches("")) {
            nameEdited = true;
        }

        if (!bio.matches("")) {
            bioEdited = true;
        }

        if (!bioEdited && !nameEdited && imageNotSelected) {
            Toast.makeText(getApplicationContext(), "No changes!", Toast.LENGTH_SHORT).show();
            enableForm();
        } else {
            ProgressDialog progressDialog = new ProgressDialog("Updating Your Profile");
            progressDialog.setCancelable(false);
            progressDialog.show(getSupportFragmentManager(), "progress dialog");

            final String userUid = mUser.getUid();

            Map<String, Object> userDetails = new HashMap<>();

            if (nameEdited) {
                userDetails.put("name", name);
            }
            if (bioEdited) {
                userDetails.put("bio", bio);
            }

            boolean finalNameEdited = nameEdited;
            boolean finalBioEdited = bioEdited;
            if (bioEdited || nameEdited) {
                mFirestore.collection("users")
                        .document(userUid)
                        .set(userDetails, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            if (finalNameEdited && finalBioEdited) {
                                sharedPreferences.edit()
                                        .putString("u_name", name)
                                        .putString("u_bio", bio)
                                        .apply();
                            } else if (finalNameEdited) {
                                sharedPreferences.edit()
                                        .putString("u_name", name)
                                        .apply();
                            } else {
                                sharedPreferences.edit()
                                        .putString("u_bio", bio)
                                        .apply();
                            }
                            Toast.makeText(getApplicationContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                            Intent launchOptions = new Intent(getApplicationContext(), OptionsActivity.class);
                            startActivity(launchOptions);
                            finish();
                            Log.d(getString(R.string.tag_debug), "DocumentSnapshot successfully written!");
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Log.e("Failed", e.getMessage(), e);
                        });
            }

            if (!imageNotSelected) {
                final UploadTask uploadTask = mStorageReference.child("profile_images")
                        .child(userUid).putFile(getImageUri());

                uploadTask.addOnSuccessListener(taskSnapshot -> mStorageReference.child("profile_images").child(userUid)
                        .getDownloadUrl().addOnSuccessListener(uri -> {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();

                            mUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.d(getString(R.string.tag_debug), "User profile updated.");
                                            sharedPreferences.edit()
                                                    .putBoolean("profile_setup_complete", true)
                                                    .putString("u_image", uri.toString())
                                                    .apply();
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                                            Intent launchOptions = new Intent(getApplicationContext(), OptionsActivity.class);
                                            startActivity(launchOptions);
                                            finish();
                                        }
                                    });
                        })).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    enableForm();
                    Log.e("Upload Error", e.getMessage());
                });
            }
        }
    }

    private void updateProfileData() {
        disableForm();

        ProgressDialog progressDialog = new ProgressDialog("Uploading your details");
        progressDialog.setCancelable(false);
        progressDialog.show(getSupportFragmentManager(), "progress dialog");

        final String userUid = mUser.getUid();
        final String name = mNameInput.getText().toString();
        final String bio = mBioInput.getText().toString();

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", name);
        userDetails.put("bio", bio);
        userDetails.put("gender", mGenderPicker.getText().toString());
        userDetails.put("date_of_birth", mDateOfBirthInput.getText().toString());
        userDetails.put("profile_setup_complete", true);

        mFirestore.collection("users")
                .document(userUid)
                .set(userDetails, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    sharedPreferences.edit()
                            .putString("u_name", name)
                            .putString("u_bio", bio)
                            .apply();
                    Log.d(getString(R.string.tag_debug), "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e("Failed", e.getMessage(), e);
                });

        final UploadTask uploadTask = mStorageReference.child("profile_images")
                .child(userUid).putFile(getImageUri());

        uploadTask.addOnSuccessListener(taskSnapshot -> mStorageReference.child("profile_images").child(userUid)
                .getDownloadUrl().addOnSuccessListener(uri -> {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(uri)
                            .build();

                    mUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(getString(R.string.tag_debug), "User profile updated.");
                                    sharedPreferences.edit()
                                            .putBoolean("profile_setup_complete", true)
                                            .putString("u_image", uri.toString())
                                            .apply();
                                    progressDialog.dismiss();
                                    Intent launchOptions = new Intent(getApplicationContext(), OptionsActivity.class);
                                    startActivity(launchOptions);
                                    finish();
                                }
                            });
                })).addOnFailureListener(e -> {
            progressDialog.dismiss();
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
            return Uri.fromFile(compressImage(tmp, getApplicationContext()));
        } catch (IOException exp) {
            exp.printStackTrace();
        }

        return Uri.fromFile(tmp);
    }
}
