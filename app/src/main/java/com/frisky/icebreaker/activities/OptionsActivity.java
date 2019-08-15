package com.frisky.icebreaker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.frisky.icebreaker.BuildConfig;
import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.assistant.RoundRectTransformation;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class OptionsActivity extends AppCompatActivity implements UIActivity {

    ImageButton mBackButton;
    ImageView mToolbarLogo;
    TextView mToolbarText;
    Button mLogoutButton;
    Button mProfileButton;

    ImageView mProfileImage;
    TextView mNameText;
    TextView mBioText;

    TextView mVersionText;

    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    @Override
    public void initUI() {
        mBackButton = findViewById(R.id.button_app_bar_left);
        mBackButton.setImageResource(R.drawable.ic_arrow_back);
        mBackButton.setOnClickListener(v -> OptionsActivity.super.onBackPressed());

        mToolbarLogo = findViewById(R.id.image_logo_text);
        mToolbarLogo.setVisibility(View.GONE);

        mProfileImage = findViewById(R.id.image_profile);
        mNameText = findViewById(R.id.text_name);
        mBioText = findViewById(R.id.text_bio);
        mProfileButton = findViewById(R.id.button_profile);

        mProfileImage.setVisibility(View.GONE);
        mNameText.setVisibility(View.GONE);
        mBioText.setVisibility(View.GONE);
        mProfileButton.setText(getString(R.string.setup_your_profile));
        mProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SetupProfileActivity.class));
            finish();
        });

        mToolbarText = findViewById(R.id.text_app_bar);
        mToolbarText.setText(R.string.options);
        mToolbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.museosans700);
        mToolbarText.setTypeface(typeface);

        mLogoutButton = findViewById(R.id.button_logout);
        mLogoutButton.setOnClickListener(v -> {
            mAuth.signOut();

            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                Intent signOutIntent = new Intent(getApplicationContext(), SignInActivity.class);
                signOutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(signOutIntent);
                finish();
            }
        });

        mVersionText = findViewById(R.id.text_version);
        String version = BuildConfig.VERSION_NAME;
        mVersionText.setText(version);

        checkForProfileSetup();
    }

    private void checkForProfileSetup() {
        if (sharedPreferences.getBoolean("profile_setup_complete", false)) {
            mProfileImage.setVisibility(View.VISIBLE);
            mNameText.setVisibility(View.VISIBLE);
            mBioText.setVisibility(View.VISIBLE);
            mProfileButton.setText(getString(R.string.edit_profile));
            mProfileButton.setOnClickListener(null);

            FirebaseUser user = mAuth.getCurrentUser();

            if (user == null)
                return;

            StorageReference profileImageRef = FirebaseStorage.getInstance().getReference()
                    .child("profile_images").child(mAuth.getCurrentUser().getUid());

            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).transform(new RoundRectTransformation()).into(mProfileImage);
                Log.d(getString(R.string.tag_debug), "Image " + uri.toString());
            }).addOnFailureListener(e -> Log.e("Uri Download Failed", e.getMessage()));

            FirebaseFirestore.getInstance().collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document == null) {
                                return;
                            }
                            mNameText.setText(document.getString("name"));
                            mBioText.setText(document.getString("bio"));
                        }
                    });
        }
    }
}
