package com.frisky.icebreaker.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.fragments.DineFragment;
import com.frisky.icebreaker.fragments.HomeFragment;
import com.frisky.icebreaker.fragments.VisitsFragment;
import com.frisky.icebreaker.interfaces.UIActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements UIActivity, BottomNavigationView.OnNavigationItemSelectedListener {

    Button mBottomSheetButton;

    TextView mBottomSheetTitle;
    TextView mBottomSheetInfo;
    TextView mBottomSheetDetails;

    SharedPreferences sharedPreferences;

    ExtendedFloatingActionButton mScanQRCodeFAB;
    BottomNavigationView navigation;

    View mBottomSheet;
    BottomSheetBehavior mBottomSheetBehaviour;

    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            Log.d(getString(R.string.tag_debug), "Saved Entry " + entry.getKey() + " " + entry.getValue());
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(getString(R.string.tag_debug), "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = Objects.requireNonNull(task.getResult()).getToken();

                    Map<String, Object> userDetails = new HashMap<>();
                    userDetails.put("firebase_instance_id", token);

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(mUser.getUid())
                            .set(userDetails, SetOptions.merge())
                            .addOnCompleteListener(task1 -> Log.d(getString(R.string.tag_debug), "Instance ID Updated"))
                    .addOnFailureListener(e -> Log.e(getString(R.string.tag_debug), "Instance ID Update failed."));

                });

        switchFragment("home");
        initUI();

        checkForProfileSetup();
    }

    @Override
    protected void onResume() {
        super.onResume();

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

        mBottomSheetBehaviour = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheetTitle = findViewById(R.id.text_sheet_title);
        mBottomSheetInfo = findViewById(R.id.text_info);
        mBottomSheetDetails = findViewById(R.id.text_details);
        mBottomSheetButton = findViewById(R.id.button_menu);
        mScanQRCodeFAB = findViewById(R.id.fab_scan_qr);

        navigation = findViewById(R.id.bottom_nav);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    private void checkForProfileSetup() {
        final String userID = mUser.getUid();

        FirebaseFirestore.getInstance().collection("users")
                .document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot == null) return;
                        boolean profileSetup = false;
                        if (snapshot.contains("profile_setup_complete")) {
                            profileSetup = snapshot.getBoolean("profile_setup_complete");
                            if (profileSetup) {
                                if (snapshot.contains("name") && snapshot.contains("bio")) {
                                    sharedPreferences.edit().putString("u_name", snapshot.getString("name")).apply();
                                    sharedPreferences.edit().putString("u_bio", snapshot.getString("bio")).apply();

                                    StorageReference profileImageRef = FirebaseStorage.getInstance().getReference()
                                            .child("profile_images").child(userID);

                                    profileImageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                            sharedPreferences.edit().putString("u_image",
                                                    uri.toString()).apply()).addOnFailureListener(e ->
                                            Log.e("Uri Download Failed", e.getMessage()));
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
        } else {
            disableSession();
        }
    }

    private void setupSessionDetails() {
        mScanQRCodeFAB.setVisibility(View.GONE);
        navigation.getOrCreateBadge(R.id.menu_dine);

        BadgeDrawable dineBadge = navigation.getBadge(R.id.menu_dine);
        assert dineBadge != null;
        dineBadge.setBackgroundColor(getColor(R.color.greenAccent));

        if (sharedPreferences.contains("bill_requested")) {
            mBottomSheetTitle.setText(getString(R.string.bill_requested));
            mBottomSheetInfo.setText(getString(R.string.bill_amount_to_be_paid));
            float amountPayable = Float.parseFloat(sharedPreferences.getString("amount_payable", "0.00"));
            @SuppressLint("DefaultLocale")
            String amount = String.format("%.2f", amountPayable);
            String billAmountString = getString(R.string.rupee) + amount;
            mBottomSheetDetails.setText(billAmountString);
            mBottomSheetButton.setVisibility(View.INVISIBLE);
        } else {
            mBottomSheetInfo.setText(sharedPreferences.getString("restaurant_name", ""));
            mBottomSheetDetails.setText(sharedPreferences.getString("table_name", ""));
            Intent resumeSession = new Intent(getApplicationContext(), MenuActivity.class);
            mBottomSheetButton.setOnClickListener(v -> startActivity(resumeSession));
        }

        mBottomSheetBehaviour = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void disableSession() {
        navigation.removeBadge(R.id.menu_dine);
        mScanQRCodeFAB.setVisibility(View.VISIBLE);
        mScanQRCodeFAB.setOnClickListener(v -> startActivity(new Intent(this, QRScanActivity.class)));
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void switchFragment(String fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment homeFragment = fragmentManager.findFragmentByTag("home");
        Fragment dineFragment = fragmentManager.findFragmentByTag("dine");
        Fragment visitsFragment = fragmentManager.findFragmentByTag("hist");

        switch (fragment) {
            case "home":
                if(homeFragment != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(homeFragment).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().add(R.id.home_activity_fragment, new HomeFragment(), "home").commit();
                }
                if(dineFragment != null && dineFragment.isVisible()){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(dineFragment).commit();
                }
                if(visitsFragment != null && visitsFragment.isVisible()){
                    //if the other fragment is visible, hide it.
                    fragmentManager.beginTransaction().hide(visitsFragment).commit();
                }
                break;
            case "dine":
                if(dineFragment != null) {
                    fragmentManager.beginTransaction().show(dineFragment).commit();
                } else {
                    fragmentManager.beginTransaction().add(R.id.home_activity_fragment, new DineFragment(), "dine").commit();
                }
                if(homeFragment != null && homeFragment.isVisible()){
                    fragmentManager.beginTransaction().hide(homeFragment).commit();
                }
                if(visitsFragment != null && visitsFragment.isVisible()){
                    fragmentManager.beginTransaction().hide(visitsFragment).commit();
                }
                break;
            case "hist":
                if(visitsFragment != null) {
                    fragmentManager.beginTransaction().show(visitsFragment).commit();
                } else {
                    fragmentManager.beginTransaction().add(R.id.home_activity_fragment, new VisitsFragment(), "hist").commit();
                }
                if(dineFragment != null && dineFragment.isVisible()){
                    fragmentManager.beginTransaction().hide(dineFragment).commit();
                }
                if(homeFragment != null && homeFragment.isVisible()){
                    fragmentManager.beginTransaction().hide(homeFragment).commit();
                }
                break;
        }
    }
}
