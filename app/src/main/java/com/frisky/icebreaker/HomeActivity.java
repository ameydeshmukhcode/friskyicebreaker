package com.frisky.icebreaker;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.orders.MenuActivity;
import com.frisky.icebreaker.orders.QRScanActivity;
import com.frisky.icebreaker.profile.ProfileActivity;
import com.frisky.icebreaker.restaurants.RestaurantViewFragment;
import com.frisky.icebreaker.social.IceBreakerFragment;
import com.frisky.icebreaker.social.SocialFragment;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.frisky.icebreaker.orders.OrderingAssistant.SESSION_ACTIVE;

public class HomeActivity extends AppCompatActivity implements UIActivity {

    ConstraintLayout bottomSheet;
    Button viewMenuButton;
    TextView restaurantName;
    TextView tableName;

    Intent resumeSession;

    ImageButton mBottomNavOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        resumeSession = new Intent(getApplicationContext(), MenuActivity.class);
        initUI();
        loadFragment(new RestaurantViewFragment());
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

        bottomSheet = findViewById(R.id.bottom_sheet_session);
        bottomSheet.setVisibility(View.GONE);

        mToolbarText = findViewById(R.id.text_app_bar);
        mToolbarText.setText(R.string.app_name);
        mToolbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.ktfroadstar);
        mToolbarText.setTypeface(typeface);

        mSocialButton = findViewById(R.id.button_app_bar_right);
        mSocialButton.setImageResource(R.drawable.round_chat_24);
        mSocialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SocialFragment());
            }
        });

        mScanQRCodeButton = findViewById(R.id.button_app_bar_left);
        mScanQRCodeButton.setImageResource(R.drawable.round_qr_code);
        mScanQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QRScanActivity.class));
            }
        });

        mBottomNavHomeButton = findViewById(R.id.button_nav_left);
        mBottomNavHomeButton.setImageResource(R.drawable.round_home_24);
        mBottomNavHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new RestaurantViewFragment());
            }
        });

        mBottomNavOrderButton = findViewById(R.id.button_nav_centre_left);
        mBottomNavOrderButton.setEnabled(false);
        mBottomNavOrderButton.setImageResource(R.drawable.round_receipt_24);
        mBottomNavOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(resumeSession);
            }
        });

        mBottomNavNotificationButton = findViewById(R.id.button_nav_centre_right);
        mBottomNavNotificationButton.setImageResource(R.drawable.round_notifications_none_24);

        mBottomNavProfileButton = findViewById(R.id.button_nav_right);
        mBottomNavProfileButton.setImageResource(R.drawable.round_person_24);
        mBottomNavProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startProfileActivity = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(startProfileActivity);
            }
        });

        mIceBreakerButton = findViewById(R.id.button_icebreaker);
        mIceBreakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new IceBreakerFragment());
            }
        });
    }

    private void checkSessionStatus() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.contains("session_active")) {
                                SESSION_ACTIVE = (boolean) document.get("session_active");
                                if (SESSION_ACTIVE) {

                                    restaurantName = findViewById(R.id.text_restaurant);
                                    tableName = findViewById(R.id.text_table);

                                    getSessionDetails();

                                    viewMenuButton = findViewById(R.id.button_view_menu);
                                    viewMenuButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(resumeSession);
                                        }
                                    });
                                }
                            }
                            else {
                                SESSION_ACTIVE = false;
                            }
                            mBottomNavOrderButton.setEnabled(true);
                        }
                    }
                });
    }

    private void getSessionDetails() {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference userRef = firebaseFirestore
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.contains("restaurant") && doc.contains("current_session")) {
                        final String restaurant = doc.getString("restaurant");
                        final String currentSession = doc.getString("current_session");

                        DocumentReference restaurantRef = firebaseFirestore
                                .collection("restaurants")
                                .document(restaurant);

                        restaurantRef.get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (doc.contains("name")) {
                                            restaurantName.setText(doc.getString("name"));
                                        }
                                    }

                                    DocumentReference sessionRef = firebaseFirestore
                                            .collection("restaurants")
                                            .document(restaurant)
                                            .collection("sessions")
                                            .document(currentSession);

                                    sessionRef.get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot doc = task.getResult();
                                                    if (doc.contains("table_id")) {
                                                        final String tableid = doc.getString("table_id");

                                                        DocumentReference tableRef = firebaseFirestore
                                                                .collection("restaurants")
                                                                .document(restaurant)
                                                                .collection("tables")
                                                                .document(tableid);

                                                        tableRef.get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot doc = task.getResult();
                                                                        String tableSerial = "";
                                                                        if (doc.contains("number")) {
                                                                            tableSerial = "Table " + doc.get("number");
                                                                            tableName.setText(tableSerial);
                                                                        }
                                                                        bottomSheet.setVisibility(View.VISIBLE);
                                                                        resumeSession.putExtra("restaurant_id", restaurant);
                                                                        resumeSession.putExtra("restaurant_name", restaurantName.getText().toString());
                                                                        resumeSession.putExtra("table_number", tableSerial);
                                                                    }
                                                                }
                                                            });
                                                    }
                                                }
                                            }
                                        });
                                }
                            });
                    }
                }
            }
        });
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
