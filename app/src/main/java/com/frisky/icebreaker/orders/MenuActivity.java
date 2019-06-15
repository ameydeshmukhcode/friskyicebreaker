package com.frisky.icebreaker.orders;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UIActivity;

public class MenuActivity extends AppCompatActivity implements UIActivity {

    ImageButton mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("qr_code_scanned")){
            setContentView(R.layout.activity_menu);
        }
        else {
            setContentView(R.layout.activity_menu_empty_state);
        }

        initUI();
    }

    @Override
    public void initUI() {
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuActivity.super.onBackPressed();
            }
        });

        if (getIntent().hasExtra("qr_code_scanned")){
            MenuFragment menuFragment = new MenuFragment();

            if (getIntent().hasExtra("restaurant_id")) {
                String restID = getIntent().getStringExtra("restaurant_id");
                Bundle bundle = new Bundle();
                bundle.putString("restaurant_id", restID);
                menuFragment.setArguments(bundle);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_menu, menuFragment)
                    .commit();
        }
    }
}
