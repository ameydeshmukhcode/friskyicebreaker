package com.frisky.icebreaker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.frisky.icebreaker.ui.base.FormActivity;
import com.frisky.icebreaker.profile.SetupProfileActivity;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements FormActivity, UIActivity {

    private static final int RC_SIGN_IN = 1;
    Button mLoginButton;
    ImageButton mGoogleButton;
    TextView mSignUpLink;

    EditText mEmailInput;
    EditText mPasswordInput;

    FirebaseAuth mAuth;
    
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            verifyLogin();
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initUI();
    }

    private void verifyLogin() {
        FirebaseUser user = mAuth.getCurrentUser();

        boolean emailVerified = user.isEmailVerified();

        if (!emailVerified) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("D", "Email sent.");
                                Toast.makeText(LoginActivity.this, "Verification Email Sent.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }

        if (user.getDisplayName() != null) {
            Intent launchHome = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(launchHome);
            finish();
        }
        else {
            Intent setupProfile = new Intent(getApplicationContext(), SetupProfileActivity.class);
            startActivity(setupProfile);
        }
    }

    public void initUI() {
        mLoginButton = findViewById(R.id.button_login);
        mGoogleButton = findViewById(R.id.button_google);
        mSignUpLink = findViewById(R.id.link_sign_up);

        mEmailInput = findViewById(R.id.input_email);
        mPasswordInput = findViewById(R.id.input_password);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

//        mGoogleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                googleSignIn();
//            }
//        });

        mSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startSignUpActivity = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(startSignUpActivity);
            }
        });
    }

    private void handleLogin() {
        if (validateForm()){
            mAuth.signInWithEmailAndPassword(mEmailInput.getText().toString(), mPasswordInput.getText().toString())
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                verifyLogin();
                            } else {
                                Log.w("Warning", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Warning", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent launchHome = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(launchHome);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Warning", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean validateForm() {
        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();

        if (email.matches("")) {
            Toast.makeText(LoginActivity.this, "Enter email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.matches("")) {
            Toast.makeText(LoginActivity.this, "Enter password",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
