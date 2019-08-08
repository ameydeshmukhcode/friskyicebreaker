package com.frisky.icebreaker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.frisky.icebreaker.notifications.NotificationsFragment;
import com.frisky.icebreaker.orders.MenuActivity;
import com.frisky.icebreaker.orders.QRScanActivity;
import com.frisky.icebreaker.profile.ProfileActivity;
import com.frisky.icebreaker.restaurants.RestaurantViewFragment;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

public class HomeActivity extends AppCompatActivity implements UIActivity, BottomNavigationView.OnNavigationItemSelectedListener {

    ConstraintLayout mBottomSheet;
    Button mBottomSheetButton;

    TextView mBottomSheetTitle;
    TextView mBottomSheetInfo;
    TextView mBottomSheetDetails;

    Intent mResumeSessionIntent;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mResumeSessionIntent = new Intent(getApplicationContext(), MenuActivity.class);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            Log.d(getString(R.string.tag_debug), "Saved Entry " + entry.getKey() + " " + entry.getValue());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        checkSessionDetails();
                    }
                },
                new IntentFilter("SessionEnd"));

        loadFragment(new RestaurantViewFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();

        initUI();

        checkSessionDetails();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.bottom_nav_home:
                loadFragment(new RestaurantViewFragment());
                break;

            case R.id.bottom_nav_menu:
                if (sharedPreferences.contains("bill_requested")) {
                    Toast.makeText(getApplicationContext(), "Bill Requested.", Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(mResumeSessionIntent);
                }
                break;

            case R.id.bottom_nav_notifications:
                loadFragment(new NotificationsFragment());
                break;

            case R.id.bottom_nav_profile:
                Intent startProfileActivity = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(startProfileActivity);
                break;
        }

        return false;
    }

    public void initUI() {
//        ImageButton mSocialButton;
        ImageButton mScanQRCodeButton;
//        ImageButton mIceBreakerButton;

        mBottomSheet = findViewById(R.id.bottom_sheet_session);
        mBottomSheet.setVisibility(View.GONE);

        mBottomSheetTitle = findViewById(R.id.text_sheet_title);
        mBottomSheetInfo = findViewById(R.id.text_info);
        mBottomSheetDetails = findViewById(R.id.text_details);
        mBottomSheetButton = findViewById(R.id.button_menu);

//        mSocialButton = findViewById(R.id.button_app_bar_right);
//        mSocialButton.setImageResource(R.drawable.ic_chat);
//        mSocialButton.setOnClickListener(v -> loadFragment(new SocialFragment()));

        mScanQRCodeButton = findViewById(R.id.button_app_bar_left);
        mScanQRCodeButton.setImageResource(R.drawable.ic_qr_code);
        mScanQRCodeButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), QRScanActivity.class)));

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

//        mIceBreakerButton = findViewById(R.id.button_icebreaker);
//        mIceBreakerButton.setOnClickListener(v -> loadFragment(new IceBreakerFragment()));
    }

    private void checkSessionDetails() {
        boolean isSessionActive = sharedPreferences.getBoolean("session_active", false);
        if (isSessionActive) {
            setupSessionDetails();
        }
        else {
            disableSession();
        }
    }

    private void setupSessionDetails() {
        if (sharedPreferences.contains("bill_requested")) {
            mBottomSheetTitle.setText(getString(R.string.bill_requested));
            mBottomSheetInfo.setText(getString(R.string.bill_amount_to_be_paid));
            String billAmountString = getString(R.string.rupee) +
                    sharedPreferences.getInt("bill_amount", 0);
            mBottomSheetDetails.setText(billAmountString);
            mBottomSheetButton.setVisibility(View.INVISIBLE);
        }
        else {
            mBottomSheetInfo.setText(sharedPreferences.getString("restaurant_name", ""));
            mBottomSheetDetails.setText(sharedPreferences.getString("table_name", ""));
            mBottomSheetButton.setOnClickListener(v -> startActivity(mResumeSessionIntent));
        }

        mBottomSheet.setVisibility(View.VISIBLE);
    }

    private void disableSession() {
        mBottomSheet.setVisibility(View.GONE);
    }

    private void loadFragment(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().getFragment(Bundle.EMPTY, "");

        if (currentFragment != null)
            Log.d("Current Frag", currentFragment.toString());

        Log.d("Change To", fragment.toString());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_activity_fragment, fragment)
                .commit();
    }
}
