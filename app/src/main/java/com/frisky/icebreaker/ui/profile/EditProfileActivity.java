package com.frisky.icebreaker.ui.profile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.frisky.icebreaker.R;

public class EditProfileActivity extends AppCompatActivity {

    Spinner mSpinnerGender;
    Spinner mSpinnerInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerGender = findViewById(R.id.spinner_gender);
        mSpinnerInterest = findViewById(R.id.spinner_interest);

//        ArrayAdapter<CharSequence> spinnerGenderAdapter = ArrayAdapter
//                .createFromResource(this, R.array.genders,
//                        android.R.layout.simple_spinner_item);
//
//        mSpinnerGender.setAdapter(spinnerGenderAdapter);
//
//        ArrayAdapter<CharSequence> spinnerInterestAdapter = ArrayAdapter
//                .createFromResource(this, R.array.genders,
//                        android.R.layout.simple_spinner_item);
//
//        mSpinnerInterest.setAdapter(spinnerInterestAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile_activity, menu);
        return true;
    }
}
