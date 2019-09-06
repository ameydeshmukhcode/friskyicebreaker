package com.frisky.icebreaker.activities;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.fragments.DiningFragment;
import com.frisky.icebreaker.fragments.OrderHistoryFragment;
import com.frisky.icebreaker.fragments.RestaurantViewFragment;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class HomeActivity extends AppCompatActivity implements UIActivity, BottomNavigationView.OnNavigationItemSelectedListener {

    ConstraintLayout mBottomSheet;
    Button mBottomSheetButton;

    TextView mBottomSheetTitle;
    TextView mBottomSheetInfo;
    TextView mBottomSheetDetails;

    Intent mResumeSessionIntent;

    SharedPreferences sharedPreferences;

    ExtendedFloatingActionButton mScanQRCodeFAB;
    BottomNavigationView navigation;

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

        switchFragment("home");

        checkForProfileSetup();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initUI();

        checkSessionDetails();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        switch (menuItem.getItemId()) {
            case R.id.menu_home:
                switchFragment("home");
                break;

            case R.id.menu_dine:
                switchFragment("dine");
                break;

            case R.id.menu_visits:
                switchFragment("hist");
                break;
        }

        return false;
    }

    public void initUI() {
        mBottomSheet = findViewById(R.id.bottom_sheet_session);
        mBottomSheet.setVisibility(View.GONE);

        mBottomSheetTitle = findViewById(R.id.text_sheet_title);
        mBottomSheetInfo = findViewById(R.id.text_info);
        mBottomSheetDetails = findViewById(R.id.text_details);
        mBottomSheetButton = findViewById(R.id.button_menu);
        mScanQRCodeFAB = findViewById(R.id.fab_scan_qr);

        navigation = findViewById(R.id.bottom_nav);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    private void checkForProfileSetup() {
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot == null)
                            return;
                        boolean profileSetup = false;
                        if (snapshot.contains("profile_setup_complete")) {
                            profileSetup = snapshot.getBoolean("profile_setup_complete");
                            if (profileSetup) {
                                if (snapshot.contains("name") && snapshot.contains("bio")) {
                                    sharedPreferences.edit().putString("u_name", snapshot.getString("name")).apply();
                                    sharedPreferences.edit().putString("u_bio", snapshot.getString("bio")).apply();

                                    StorageReference profileImageRef = FirebaseStorage.getInstance().getReference()
                                            .child("profile_images").child(userID);

                                    profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        sharedPreferences.edit().putString("u_image", uri.toString()).apply();
                                    }).addOnFailureListener(e -> Log.e("Uri Download Failed", e.getMessage()));
                                }
                            }
                        }
                        sharedPreferences.edit().putBoolean("profile_setup_complete", profileSetup).apply();
                    }
                });
    }

    private void checkSessionDetails() {
        Fragment dineFragment = getSupportFragmentManager().findFragmentByTag("dine");

        if (dineFragment != null) {
            getSupportFragmentManager().beginTransaction()
                .detach(dineFragment)
                .attach(dineFragment)
                .commit();
        }

        boolean isSessionActive = sharedPreferences.getBoolean("session_active", false);
        if (isSessionActive) {
            setupSessionDetails();
        }
        else {
            disableSession();
        }
    }

    private void setupSessionDetails() {
        mScanQRCodeFAB.setVisibility(View.GONE);
        navigation.getOrCreateBadge(R.id.menu_dine);
        navigation.getBadge(R.id.menu_dine).setBackgroundColor(getColor(R.color.appGreen));
        if (sharedPreferences.contains("bill_requested")) {
            mBottomSheetTitle.setText(getString(R.string.bill_requested));
            mBottomSheetInfo.setText(getString(R.string.bill_amount_to_be_paid));
            @SuppressLint("DefaultLocale")
            String amount = String.format("%.2f",
                    Double.parseDouble(sharedPreferences.getString("amount_payable", "")));
            String billAmountString = getString(R.string.rupee) + amount;
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
        navigation.removeBadge(R.id.menu_dine);
        mScanQRCodeFAB.setVisibility(View.VISIBLE);
        mScanQRCodeFAB.setOnClickListener(v -> startActivity(new Intent(this, QRScanActivity.class)));
        mBottomSheet.setVisibility(View.GONE);
    }

    private void switchFragment(String fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager == null)
            return;

        switch (fragment) {
            case "home":
                if(fragmentManager.findFragmentByTag("home") != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("home")).commit();
                }
                else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().add(R.id.home_activity_fragment, new RestaurantViewFragment(), "home").commit();
                }
                if(fragmentManager.findFragmentByTag("dine") != null
                        && fragmentManager.findFragmentByTag("dine").isVisible()){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("dine")).commit();
                }
                if(fragmentManager.findFragmentByTag("hist") != null
                        && fragmentManager.findFragmentByTag("hist").isVisible()){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("hist")).commit();
                }
                break;
            case "dine":
                if(fragmentManager.findFragmentByTag("dine") != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("dine")).commit();
                }
                else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().add(R.id.home_activity_fragment, new DiningFragment(), "dine").commit();
                }
                if(fragmentManager.findFragmentByTag("home") != null
                        && fragmentManager.findFragmentByTag("home").isVisible()){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("home")).commit();
                }
                if(fragmentManager.findFragmentByTag("hist") != null
                        && fragmentManager.findFragmentByTag("hist").isVisible()){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("hist")).commit();
                }
                break;
            case "hist":
                if(fragmentManager.findFragmentByTag("hist") != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("hist")).commit();
                }
                else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().add(R.id.home_activity_fragment, new OrderHistoryFragment(), "hist").commit();
                }
                if(fragmentManager.findFragmentByTag("dine") != null
                        && fragmentManager.findFragmentByTag("dine").isVisible()){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("dine")).commit();
                }
                if(fragmentManager.findFragmentByTag("home") != null
                        && fragmentManager.findFragmentByTag("home").isVisible()){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("home")).commit();
                }
                break;
        }
    }
}
