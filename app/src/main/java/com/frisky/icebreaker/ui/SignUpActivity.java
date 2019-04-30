package com.frisky.icebreaker.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.FormActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity implements FormActivity {

    Button mSignUpButton;
    EditText mUsernameInput;
    EditText mPasswordInput;
    EditText mNameInput;
    EditText mConfirmPasswordInput;
    CheckBox mAgeCheck;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    private void initUI() {
        mSignUpButton = findViewById(R.id.button_sign_up);

        mUsernameInput = findViewById(R.id.input_username);
        mPasswordInput = findViewById(R.id.input_password);
        mNameInput = findViewById(R.id.input_name);
        mConfirmPasswordInput = findViewById(R.id.input_confirm_password);
        mAgeCheck = findViewById(R.id.checkbox_age);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO handle SignUp operation here
                handleSignUp(mUsernameInput.getText().toString(), mPasswordInput.getText().toString());
            }
        });
    }

    private void handleSignUp(String email, String password) {
        if (validateForm()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(mNameInput.getText().toString())
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("updated", "User profile updated.");
                                                }
                                            }
                                        });

                                FirebaseAuth.getInstance().signOut();
                                Intent startLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(startLoginActivity);
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to create account.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public boolean validateForm() {
        String email = mUsernameInput.getText().toString();
        String password = mPasswordInput.getText().toString();
        String name = mNameInput.getText().toString();
        String confirmPassword = mConfirmPasswordInput.getText().toString();

        if (name.matches("")) {
            Toast.makeText(SignUpActivity.this, "Enter name",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.matches("")) {
            Toast.makeText(SignUpActivity.this, "Enter email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.matches("")) {
            Toast.makeText(SignUpActivity.this, "Enter password",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Passwords don't match",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!mAgeCheck.isChecked()) {
            Toast.makeText(SignUpActivity.this, "Confirm that you're older than 18",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
