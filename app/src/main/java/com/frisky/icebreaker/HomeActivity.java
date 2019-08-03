package com.frisky.icebreaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.frisky.icebreaker.notifications.NotificationsFragment;
import com.frisky.icebreaker.orders.MenuActivity;
import com.frisky.icebreaker.orders.QRScanActivity;
import com.frisky.icebreaker.profile.ProfileActivity;
import com.frisky.icebreaker.restaurants.RestaurantViewFragment;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class HomeActivity extends AppCompatActivity implements UIActivity, BottomNavigationView.OnNavigationItemSelectedListener {

    ConstraintLayout mBottomSheet;
    Button mViewMenuButton;

    TextView mRestaurantName;
    TextView mTableName;

    Intent mResumeSessionIntent;

    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mResumeSessionIntent = new Intent(getApplicationContext(), MenuActivity.class);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            Log.d(getString(R.string.tag_debug), "Saved Entry " + entry.getKey());
        }

        initUI();
        loadFragment(new RestaurantViewFragment());
        addListenerForSessionChange();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSessionDetails();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.bottom_nav_home:
                loadFragment(new RestaurantViewFragment());
                break;

            case R.id.bottom_nav_menu:
                startActivity(mResumeSessionIntent);
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

        mRestaurantName = findViewById(R.id.text_restaurant);
        mTableName = findViewById(R.id.text_table);

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

    private void addListenerForSessionChange() {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
                .document(mUser.getUid());

        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Failed", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                boolean sessionActive = false;
                if (snapshot.contains("session_active")) {
                    sessionActive = (boolean) snapshot.get("session_active");
                }

                if (sessionActive) {
                    enableSession();
                }
                else {
                    disableSession();
                }

                Log.d("Snapshot Exists", "Current data: " + snapshot.getData());
            }
            else {
                Log.d("No Snapshot", "Current data: null");
            }
        });
    }

    private void checkSessionDetails() {
        boolean isSessionActive = sharedPreferences.getBoolean("session_active", false);
        if (isSessionActive) {
            setupSessionDetails();
        }
    }

    private void setupSessionDetails() {
        mTableName.setText(sharedPreferences.getString("table_serial", ""));
        mRestaurantName.setText(sharedPreferences.getString("restaurant_name", ""));
        mBottomSheet.setVisibility(View.VISIBLE);
        mResumeSessionIntent.putExtra("restaurant_id", sharedPreferences.getString("restaurant", ""));
        mResumeSessionIntent.putExtra("restaurant_name", mRestaurantName.getText().toString());
        mResumeSessionIntent.putExtra("table_number", sharedPreferences.getString("table_serial", ""));
    }

    private void enableSession() {
        mRestaurantName = findViewById(R.id.text_restaurant);
        mTableName = findViewById(R.id.text_table);

        setupSessionDetails();

        mViewMenuButton = findViewById(R.id.button_menu);
        mViewMenuButton.setOnClickListener(v -> startActivity(mResumeSessionIntent));
    }

    private void disableSession() {
        mBottomSheet.setVisibility(View.GONE);
        sharedPreferences.edit()
                .putBoolean("session_active", false)
                .remove("restaurant")
                .remove("restaurant_name")
                .remove("current_session")
                .remove("table_serial")
                .apply();
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
