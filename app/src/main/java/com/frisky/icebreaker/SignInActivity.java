package com.frisky.icebreaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;

    ConstraintLayout mUseEmailButton;
    ConstraintLayout mUseGoogleButton;

    FirebaseAuth mAuth;
    
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_sign_in);

        mUseEmailButton = findViewById(R.id.button_use_email);
        mUseEmailButton.setOnClickListener(v -> startActivity(new Intent(this, SignInEmailActivity.class)));

        mUseGoogleButton = findViewById(R.id.button_use_google);
        mUseGoogleButton.setOnClickListener(v -> googleSignIn());

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            verifyLogin();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
            }
            catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Warning", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Intent launchHome = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(launchHome);
                        finish();
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        Log.w("Warning", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void verifyLogin() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            Intent launchHome = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(launchHome);
            finish();
        }
    }
}
