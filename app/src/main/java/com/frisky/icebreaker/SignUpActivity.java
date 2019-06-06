package com.frisky.icebreaker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.frisky.icebreaker.ui.base.FormActivity;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUpActivity extends AppCompatActivity implements FormActivity, UIActivity {

    Button mSignUpButton;
    EditText mEmailInput;
    EditText mPasswordInput;
    EditText mConfirmPasswordInput;
    CheckBox mAgeCheck;
    TextView mErrorText;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    public void initUI() {
        mSignUpButton = findViewById(R.id.button_sign_up);
        mEmailInput = findViewById(R.id.input_email);
        mPasswordInput = findViewById(R.id.input_password);
        mConfirmPasswordInput = findViewById(R.id.input_confirm_password);
        mAgeCheck = findViewById(R.id.checkbox_age);
        mErrorText = findViewById(R.id.text_error);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp(mEmailInput.getText().toString(), mPasswordInput.getText().toString());
            }
        });
    }

    private void handleSignUp(String email, String password) {
        mErrorText.setText("");
        if (validateForm()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getApplicationContext(), "Signed up with Frisky!",
                                        Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                Intent startLoginActivity = new Intent(getApplicationContext(), SignInActivity.class);
                                startActivity(startLoginActivity);
                            }
                            else if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                }
                                catch(FirebaseAuthWeakPasswordException e) {
                                    Log.e("Create User Error", e.getReason());
                                    mErrorText.setText(getString(R.string.error_weak_password));
                                }
                                catch(FirebaseAuthInvalidCredentialsException e) {
                                    Log.e("Create User Error", e.getErrorCode());
                                    mErrorText.setText(getString(R.string.error_invalid_email));
                                }
                                catch(FirebaseAuthUserCollisionException e) {
                                    Log.e("Create User Error", e.getErrorCode());
                                    mErrorText.setText(getString(R.string.error_duplicate_email));
                                }
                                catch (FirebaseNetworkException e) {
                                    Log.e("Create User Error", e.getMessage());
                                    mErrorText.setText(getString(R.string.error_network));
                                }
                                catch(Exception e) {
                                    Log.e("Create User Error", e.getMessage());
                                }
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

    @Override
    public void disableForm() {

    }

    @Override
    public void enableForm() {

    }
}
