package com.frisky.icebreaker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.frisky.icebreaker.ui.base.FormActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements FormActivity {

    Button mSignUpButton;
    EditText mEmailInput;
    EditText mPasswordInput;
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

        mEmailInput = findViewById(R.id.input_email);
        mPasswordInput = findViewById(R.id.input_password);
        mConfirmPasswordInput = findViewById(R.id.input_confirm_password);
        mAgeCheck = findViewById(R.id.checkbox_age);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp(mEmailInput.getText().toString(), mPasswordInput.getText().toString());
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
                                Toast.makeText(getApplicationContext(), "Signed up with Frisky!",
                                        Toast.LENGTH_SHORT).show();
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
        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();
        String confirmPassword = mConfirmPasswordInput.getText().toString();

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
