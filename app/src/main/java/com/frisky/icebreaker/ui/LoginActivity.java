package com.frisky.icebreaker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.frisky.icebreaker.R;

public class LoginActivity extends AppCompatActivity {

    Button mLoginButton;
    TextView mSignUpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
    }

    private void initUI() {
        mLoginButton = findViewById(R.id.button_login);
        mSignUpLink = findViewById(R.id.link_sign_up);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO handle Login operation here
                Intent startHomeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(startHomeActivity);
                finish();
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
}
