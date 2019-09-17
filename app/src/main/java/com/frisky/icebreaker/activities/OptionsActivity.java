package com.frisky.icebreaker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.frisky.icebreaker.BuildConfig;
import com.frisky.icebreaker.R;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.frisky.icebreaker.ui.assistant.RoundRectTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class OptionsActivity extends AppCompatActivity implements UIActivity {

    Button mLogoutButton;
    Button mProfileButton;

    ImageView mProfileImage;
    TextView mNameText;
    TextView mBioText;

    TextView mVersionText;

    Toolbar mToolbar;

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
        mProfileImage = findViewById(R.id.image_profile);
        mNameText = findViewById(R.id.text_name);
        mBioText = findViewById(R.id.text_bio);
        mProfileButton = findViewById(R.id.button_profile);

        mToolbar = findViewById(R.id.toolbar_options);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        mToolbar.setTitle(getString(R.string.options));

        mProfileImage.setVisibility(View.GONE);
        mNameText.setVisibility(View.GONE);
        mBioText.setVisibility(View.GONE);
        mProfileButton.setText(getString(R.string.setup_your_profile));
        mProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SetupProfileActivity.class));
            finish();
        });

        mLogoutButton = findViewById(R.id.button_logout);
        mLogoutButton.setOnClickListener(v -> {
            if (!sharedPreferences.getBoolean("session_active", false)) {
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
            }
            else {
                Toast toast = Toast.makeText(this, "You have an active session!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
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
            mProfileButton.setOnClickListener(v -> {
                Intent editProfile = new Intent(this, SetupProfileActivity.class);
                editProfile.putExtra("edit_mode", true);
                startActivity(editProfile);
                finish();
            });

            mNameText.setText(sharedPreferences.getString("u_name", ""));
            mBioText.setText(sharedPreferences.getString("u_bio", ""));

            Picasso.get().load(sharedPreferences.getString("u_image", ""))
                    .transform(new RoundRectTransformation()).into(mProfileImage);
        }
    }
}
