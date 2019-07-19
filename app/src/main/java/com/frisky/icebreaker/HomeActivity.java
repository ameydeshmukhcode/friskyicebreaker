package com.frisky.icebreaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.frisky.icebreaker.notifications.NotificationsFragment;
import com.frisky.icebreaker.orders.MenuActivity;
import com.frisky.icebreaker.orders.QRScanActivity;
import com.frisky.icebreaker.profile.ProfileActivity;
import com.frisky.icebreaker.restaurants.RestaurantViewFragment;
import com.frisky.icebreaker.social.IceBreakerFragment;
import com.frisky.icebreaker.social.SocialFragment;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity implements UIActivity {

    ConstraintLayout mBottomSheet;
    Button mViewMenuButton;

    TextView mRestaurantName;
    TextView mTableName;

    Intent mResumeSessionIntent;

    ImageButton mBottomNavOrderButton;

    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mResumeSessionIntent = new Intent(getApplicationContext(), MenuActivity.class);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        initUI();
        loadFragment(new RestaurantViewFragment());
        addListenerForSessionChange();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSessionStatus();
    }

    public void initUI() {
        ImageButton mSocialButton;
        ImageButton mScanQRCodeButton;
        ImageButton mBottomNavHomeButton;
        ImageButton mBottomNavProfileButton;
        ImageButton mBottomNavNotificationButton;
        ImageButton mIceBreakerButton;
        TextView mToolbarText;

        mBottomSheet = findViewById(R.id.bottom_sheet_session);
        mBottomSheet.setVisibility(View.GONE);

        mToolbarText = findViewById(R.id.text_app_bar);
        mToolbarText.setText(R.string.app_name);
        mToolbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.ktfroadstar);
        mToolbarText.setTypeface(typeface);

        mSocialButton = findViewById(R.id.button_app_bar_right);
        mSocialButton.setImageResource(R.drawable.round_chat_24);
        mSocialButton.setOnClickListener(v -> loadFragment(new SocialFragment()));

        mScanQRCodeButton = findViewById(R.id.button_app_bar_left);
        mScanQRCodeButton.setImageResource(R.drawable.round_qr_code);
        mScanQRCodeButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), QRScanActivity.class)));

        mBottomNavHomeButton = findViewById(R.id.button_nav_left);
        mBottomNavHomeButton.setImageResource(R.drawable.round_home_24);
        mBottomNavHomeButton.setOnClickListener(v -> loadFragment(new RestaurantViewFragment()));

        mBottomNavOrderButton = findViewById(R.id.button_nav_centre_left);
        mBottomNavOrderButton.setEnabled(false);
        mBottomNavOrderButton.setImageResource(R.drawable.round_receipt_24);
        mBottomNavOrderButton.setOnClickListener(v -> startActivity(mResumeSessionIntent));

        mBottomNavNotificationButton = findViewById(R.id.button_nav_centre_right);
        mBottomNavNotificationButton.setImageResource(R.drawable.round_notifications_none_24);
        mBottomNavNotificationButton.setOnClickListener(v -> loadFragment(new NotificationsFragment()));

        mBottomNavProfileButton = findViewById(R.id.button_nav_right);
        mBottomNavProfileButton.setImageResource(R.drawable.round_person_24);
        mBottomNavProfileButton.setOnClickListener(v -> {
            Intent startProfileActivity = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(startProfileActivity);
        });

        mIceBreakerButton = findViewById(R.id.button_icebreaker);
        mIceBreakerButton.setOnClickListener(v -> loadFragment(new IceBreakerFragment()));
    }

    private void checkSessionStatus() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null)
            return;

        firebaseFirestore.collection("users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document == null)
                            return;
                        if (document.contains("session_active")) {
                            boolean value = (boolean) document.get("session_active");
                            sharedPreferences.edit()
                                    .putBoolean("session_active", value)
                                    .apply();

                            boolean isSessionActive = getSharedPreferences(getString(R.string.app_name),
                                    MODE_PRIVATE).getBoolean("session_active", false);

                            if (isSessionActive) {
                                enableSession();
                            }
                            else {
                                disableSession();
                            }
                        }
                        else {
                            disableSession();
                        }
                    }
                });
    }

    private void getSessionDetails() {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        if (mUser == null)
            return;

        DocumentReference userRef = firebaseFirestore
                .collection("users")
                .document(mUser.getUid());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc == null)
                    return;
                if (doc.contains("restaurant") && doc.contains("current_session")) {
                    final String restaurant = doc.getString("restaurant");
                    final String currentSession = doc.getString("current_session");

                    if (restaurant == null)
                        return;

                    DocumentReference restaurantRef = firebaseFirestore
                            .collection("restaurants")
                            .document(restaurant);

                    restaurantRef.get()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot doc1 = task1.getResult();

                                if (doc1 == null)
                                    return;

                                if (doc1.contains("name")) {
                                    mRestaurantName.setText(doc1.getString("name"));
                                }
                            }

                            if (currentSession == null)
                                return;

                            DocumentReference sessionRef = firebaseFirestore
                                    .collection("restaurants")
                                    .document(restaurant)
                                    .collection("sessions")
                                    .document(currentSession);

                            sessionRef.get()
                                .addOnCompleteListener(task11 -> {
                                    if (task11.isSuccessful()) {
                                        DocumentSnapshot doc1 = task11.getResult();

                                        if (doc1 == null)
                                            return;

                                        if (doc1.contains("table_id")) {
                                            final String tableid = doc1.getString("table_id");

                                            if (tableid == null)
                                                return;

                                            DocumentReference tableRef = firebaseFirestore
                                                    .collection("restaurants")
                                                    .document(restaurant)
                                                    .collection("tables")
                                                    .document(tableid);

                                            tableRef.get()
                                                .addOnCompleteListener(task111 -> {
                                                    if (task111.isSuccessful()) {
                                                        DocumentSnapshot doc11 = task111.getResult();
                                                        String tableSerial = "";

                                                        if (doc11 == null)
                                                            return;

                                                        if (doc11.contains("number")) {
                                                            tableSerial = "Table " + doc11.get("number");
                                                            mTableName.setText(tableSerial);
                                                        }
                                                        mBottomSheet.setVisibility(View.VISIBLE);
                                                        mResumeSessionIntent.putExtra("restaurant_id", restaurant);
                                                        mResumeSessionIntent.putExtra("restaurant_name", mRestaurantName.getText().toString());
                                                        mResumeSessionIntent.putExtra("table_number", tableSerial);
                                                        mBottomNavOrderButton.setEnabled(true);
                                                    }
                                                });
                                        }
                                    }
                                });
                        });
                }
            }
        });
    }

    private void addListenerForSessionChange() {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
                .document(mUser.getUid());

        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("Failed", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                boolean sessionActive = (boolean) snapshot.get("session_active");

                boolean isSessionActive = getSharedPreferences(getString(R.string.app_name),
                        MODE_PRIVATE).getBoolean("session_active", false);
                
                if (sessionActive) {
                    enableSession();
                }
                else if (isSessionActive){
                    disableSession();
                }
                
                Log.d("Snapshot Exists", "Current data: " + snapshot.getData());
            }
            else {
                Log.d("No Snapshot", "Current data: null");
            }
        });
    }

    private void enableSession() {
        mRestaurantName = findViewById(R.id.text_restaurant);
        mTableName = findViewById(R.id.text_table);

        getSessionDetails();

        mViewMenuButton = findViewById(R.id.button_view_menu);
        mViewMenuButton.setOnClickListener(v -> startActivity(mResumeSessionIntent));
    }

    private void disableSession() {
        mBottomSheet.setVisibility(View.GONE);
        mBottomNavOrderButton.setEnabled(true);
        sharedPreferences.edit()
                .putBoolean("session_active", false)
                .apply();
    }

    private void loadFragment(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().getFragment(Bundle.EMPTY, "");

        if (currentFragment != null)
            Log.i("Current Frag", currentFragment.toString());

        Log.i("Change To", fragment.toString());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_activity_fragment, fragment)
                .commit();
    }
}
