package com.frisky.icebreaker.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.FormActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements FormActivity {

    Button mLoginButton;
    TextView mSignUpLink;

    EditText mEmailInput;
    EditText mPasswordInput;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        
        initUI();
    }

    private void initUI() {
        mLoginButton = findViewById(R.id.button_login);
        mSignUpLink = findViewById(R.id.link_sign_up);

        mEmailInput = findViewById(R.id.input_email);
        mPasswordInput = findViewById(R.id.input_password);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

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
                                Intent launchHome = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(launchHome);
                                finish();
                            } else {
                                Log.w("Warning", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
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
