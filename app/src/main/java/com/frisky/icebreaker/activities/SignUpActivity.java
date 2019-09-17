package com.frisky.icebreaker.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.interfaces.FormActivity;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.frisky.icebreaker.ui.components.dialogs.ProgressDialog;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUpActivity extends AppCompatActivity implements FormActivity, UIActivity {

    Button mSignUpButton;
    EditText mEmailInput;
    EditText mPasswordInput;
    EditText mConfirmPasswordInput;
    TextView mErrorText;
    Button mBackButton;

    FirebaseAuth mAuth;

    ProgressDialog progressDialog = new ProgressDialog("Signing up with Frisky");

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
        mErrorText = findViewById(R.id.text_error);

        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> super.onBackPressed());

        mSignUpButton.setOnClickListener(v -> handleSignUp(mEmailInput.getText().toString(), mPasswordInput.getText().toString()));
    }

    private void handleSignUp(String email, String password) {
        progressDialog.show(getSupportFragmentManager(), "signing up");
        progressDialog.setCancelable(false);
        mErrorText.setText("");
        if (validateForm()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Signed up with Frisky!",
                                    Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            // go back to SignInActivity and finish this
                            progressDialog.dismiss();
                            super.onBackPressed();
                            finish();
                        }
                        else if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            try {
                                if (task.getException() != null)
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
                    });
        }
        else {
            progressDialog.dismiss();
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

        return true;
    }

    @Override
    public void disableForm() {

    }

    @Override
    public void enableForm() {

    }
}
