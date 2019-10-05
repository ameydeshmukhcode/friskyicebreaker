package com.frisky.icebreaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.interfaces.FormActivity;
import com.frisky.icebreaker.ui.components.dialogs.ProgressDialog;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class SignInEmailActivity extends AppCompatActivity implements FormActivity {

    Button mSignUpLink;
    Button mForgotPasswordLink;
    TextView mErrorText;
    Button mLoginButton;
    Button mBackButton;

    EditText mEmailInput;
    EditText mPasswordInput;

    FirebaseAuth mAuth;

    private int PW_ENTRY_FAILED = 0;

    ProgressDialog progressDialog = new ProgressDialog("Signing you in");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_sign_in_email);
        initUI();
    }

    @Override
    public void initUI() {
        mLoginButton = findViewById(R.id.button_login);
        mSignUpLink = findViewById(R.id.link_sign_up);
        mErrorText = findViewById(R.id.text_error);
        mForgotPasswordLink = findViewById(R.id.link_forgot_password);

        mEmailInput = findViewById(R.id.input_email);
        mPasswordInput = findViewById(R.id.input_password);
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(v -> super.onBackPressed());

        enableForm();
    }

    @Override
    public boolean validateForm() {
        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();

        if (email.matches("")) {
            Toast.makeText(this, "Enter email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.matches("")) {
            Toast.makeText(this, "Enter password",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void disableForm() {
        mLoginButton.setOnClickListener(null);
        mSignUpLink.setOnClickListener(null);
        mForgotPasswordLink.setOnClickListener(null);
        mEmailInput.setEnabled(false);
        mPasswordInput.setEnabled(false);
    }

    @Override
    public void enableForm() {
        mEmailInput.setEnabled(true);
        mPasswordInput.setEnabled(true);
        mLoginButton.setOnClickListener(v -> handleLogin());

        mSignUpLink.setOnClickListener(v -> {
            Intent startSignUpActivity = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(startSignUpActivity);
        });

        mForgotPasswordLink.setOnClickListener(v -> {
            String email = mEmailInput.getText().toString();

            if (email.equals("")) {
                Toast.makeText(this, "Enter email",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(getString(R.string.tag_debug), "Reset pw email sent.");
                            mErrorText.setText(getString(R.string.error_password_reset_email));
                        } else {
                            try {
                                if (task.getException() != null)
                                    throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Log.e("Create User Error", e.getErrorCode());
                                mErrorText.setText(getString(R.string.error_not_signed_up));
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.e("Create User Error", e.getErrorCode());
                                mErrorText.setText(getString(R.string.error_incorrect_password));
                            } catch (Exception exp) {
                                Log.e("Reset password error", exp.toString());
                            }
                        }
                    });
        });
    }

    private void handleLogin() {
        progressDialog.show(getSupportFragmentManager(), "signing in");
        progressDialog.setCancelable(false);
        mErrorText.setText("");
        mErrorText.setOnClickListener(null);
        if (validateForm()){
            mAuth.signInWithEmailAndPassword(mEmailInput.getText().toString(), mPasswordInput.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            verifyLogin();
                        } else {
                            progressDialog.dismiss();
                            try {
                                if (task.getException() != null) throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Log.e("Sign In Error", e.getErrorCode());
                                mErrorText.setText(getString(R.string.error_not_signed_up));
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.e("Sign In Error", e.getErrorCode());
                                if (e.getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                                    mErrorText.setText(getString(R.string.error_invalid_email));
                                } else if (e.getErrorCode().equals("ERROR_WRONG_PASSWORD")) {
                                    if (PW_ENTRY_FAILED > 2) {
                                        mErrorText.setText(getString(R.string.error_password_reset_hint));
                                        return;
                                    }
                                    mErrorText.setText(getString(R.string.error_incorrect_password));
                                    PW_ENTRY_FAILED++;
                                }
                            } catch (FirebaseNetworkException e) {
                                Log.e("Sign In Error", e.getMessage());
                                mErrorText.setText(getString(R.string.error_network));
                            } catch (Exception e) {
                                Log.e("Sign In Error", e.getMessage());
                            }
                            Log.e("Sign In Error", "signInWithEmail:failure", task.getException());
                        }
                    });
        } else {
            progressDialog.dismiss();
        }
    }

    private void verifyLogin() {
        final FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        boolean emailVerified = user.isEmailVerified();

        if (!emailVerified) {
            progressDialog.dismiss();
            mErrorText.setText(getString(R.string.error_email_not_verified));
            mErrorText.setOnClickListener(v -> user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(getString(R.string.tag_debug), "Verification email sent.");
                            Toast.makeText(this, "Verification Email Sent.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }));
            return;
        }

        Intent launchHome = new Intent(getApplicationContext(), HomeActivity.class);
        launchHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(launchHome);
        finish();
    }
}
