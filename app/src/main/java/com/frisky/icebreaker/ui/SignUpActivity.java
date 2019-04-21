package com.frisky.icebreaker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.frisky.icebreaker.R;

public class SignUpActivity extends AppCompatActivity {

  Button mSignUpButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);
    initUI();
  }

  private void initUI() {
    mSignUpButton = findViewById(R.id.button_sign_up);

    mSignUpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //TODO handle SignUp operation here
        Intent startLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(startLoginActivity);
      }
    });
  }
}
