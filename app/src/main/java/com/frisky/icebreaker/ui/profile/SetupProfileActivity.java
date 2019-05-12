package com.frisky.icebreaker.ui.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.FormActivity;
import com.frisky.icebreaker.ui.components.dialogs.PickImageDialog;

public class SetupProfileActivity extends AppCompatActivity implements FormActivity {

    ImageView mProfileImage;
    ImageButton mCancelButton;
    ImageButton mDoneButton;
    TextView mNameInput;

    PickImageDialog pickImageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        mNameInput = findViewById(R.id.input_name);

        mProfileImage = findViewById(R.id.image_profile);

        mCancelButton = findViewById(R.id.button_cancel);
        mDoneButton = findViewById(R.id.button_done);

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    //TODO
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
    public boolean validateForm() {
        String name = mNameInput.getText().toString();

        if (name.matches("")) {
            Toast.makeText(SetupProfileActivity.this, "Enter name",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
