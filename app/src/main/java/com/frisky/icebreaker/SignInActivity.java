package com.frisky.icebreaker;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.frisky.icebreaker.ui.base.FormActivity;
import com.frisky.icebreaker.profile.SetupProfileActivity;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements FormActivity, UIActivity {

    //private static final int RC_SIGN_IN = 1;
    private int PW_ENTRY_FAILED = 0;

    Button mLoginButton;
    //ImageButton mGoogleButton;
    TextView mSignUpLink;
    TextView mForgotPasswordLink;
    TextView mErrorText;

    EditText mEmailInput;
    EditText mPasswordInput;

    FirebaseAuth mAuth;
    
    //GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            verifyLogin();
        }
        else {
            setupActivityUI();
        }

//        Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void initUI() {
        mLoginButton = findViewById(R.id.button_login);
//        mGoogleButton = findViewById(R.id.button_google);
        mSignUpLink = findViewById(R.id.link_sign_up);
        mErrorText = findViewById(R.id.text_error);
        mForgotPasswordLink = findViewById(R.id.link_forgot_password);

        mEmailInput = findViewById(R.id.input_email);
        mPasswordInput = findViewById(R.id.input_password);

//        mGoogleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                googleSignIn();
//            }
//        });

        enableForm();
    }

    private void handleLogin() {
        mErrorText.setText("");
        mErrorText.setOnClickListener(null);
        if (validateForm()){
            mAuth.signInWithEmailAndPassword(mEmailInput.getText().toString(), mPasswordInput.getText().toString())
                    .addOnCompleteListener(SignInActivity.this, task -> {
                        if (task.isSuccessful()) {
                            verifyLogin();
                        }
                        else {
                            try {
                                if (task.getException() != null)
                                    throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException e) {
                                Log.e("Sign In Error", e.getErrorCode());
                                mErrorText.setText(getString(R.string.error_not_signed_up));
                            }
                            catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.e("Sign In Error", e.getErrorCode());
                                if (e.getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                                    mErrorText.setText(getString(R.string.error_invalid_email));
                                }
                                else if (e.getErrorCode().equals("ERROR_WRONG_PASSWORD")) {
                                    if (PW_ENTRY_FAILED > 2) {
                                        mErrorText.setText(getString(R.string.error_password_reset_hint));
                                        return;
                                    }
                                    mErrorText.setText(getString(R.string.error_incorrect_password));
                                    PW_ENTRY_FAILED++;
                                }
                            }
                            catch (FirebaseNetworkException e) {
                                Log.e("Sign In Error", e.getMessage());
                                mErrorText.setText(getString(R.string.error_network));
                            }
                            catch (Exception e) {
                                Log.e("Sign In Error", e.getMessage());
                            }
                            Log.e("Sign In Error", "signInWithEmail:failure", task.getException());
                        }
                    });
        }
    }

//    private void googleSignIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                if (account != null) {
//                    firebaseAuthWithGoogle(account);
//                }
//            }
//            catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w("Warning", "Google sign in failed", e);
//            }
//        }
//    }
//
//    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Intent launchHome = new Intent(getApplicationContext(), HomeActivity.class);
//                            startActivity(launchHome);
//                            finish();
//                        }
//                        else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("Warning", "signInWithCredential:failure", task.getException());
//                        }
//                    }
//                });
//    }

    @Override
    public boolean validateForm() {
        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();

        if (email.matches("")) {
            Toast.makeText(SignInActivity.this, "Enter email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.matches("")) {
            Toast.makeText(SignInActivity.this, "Enter password",
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
                Toast.makeText(SignInActivity.this, "Enter email",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i("Reset Password", "Email sent.");
                            mErrorText.setText(getString(R.string.error_password_reset_email));
                        }
                        else {
                            try {
                                if (task.getException() != null)
                                    throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException e) {
                                Log.e("Create User Error", e.getErrorCode());
                                mErrorText.setText(getString(R.string.error_not_signed_up));
                            }
                            catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.e("Create User Error", e.getErrorCode());
                                mErrorText.setText(getString(R.string.error_incorrect_password));
                            }
                            catch (Exception exp) {
                                Log.e("Reset password error", exp.toString());
                            }
                        }
                    });
        });
    }

    private void setupActivityUI() {
        setContentView(R.layout.activity_sign_in);
        initUI();
    }

    private void verifyLogin() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            return;
        }

        boolean emailVerified = user.isEmailVerified();

        if (!emailVerified) {
            mErrorText.setText(getString(R.string.error_email_not_verified));
            mErrorText.setOnClickListener(v -> user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i("Verification email", "Email sent.");
                            Toast.makeText(SignInActivity.this, "Verification Email Sent.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }));
            return;
        }

        if (user.getDisplayName() != null && !user.getDisplayName().equals("")) {
            Intent launchHome = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(launchHome);
            finish();
        }
        else {
            Intent setupProfile = new Intent(getApplicationContext(), SetupProfileActivity.class);
            startActivity(setupProfile);
        }
    }
}
