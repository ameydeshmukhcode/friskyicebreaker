package com.frisky.icebreaker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import com.frisky.icebreaker.notifications.NotificationsFragment;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.google.firebase.auth.FirebaseUser;
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

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        resumeSession = new Intent(getApplicationContext(), MenuActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RES";
            String description = "desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("RES", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

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

        bottomSheet = findViewById(R.id.bottom_sheet_session);
        bottomSheet.setVisibility(View.GONE);

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
        mBottomNavOrderButton.setOnClickListener(v -> startActivity(resumeSession));

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
                            SESSION_ACTIVE = (boolean) document.get("session_active");
                            if (SESSION_ACTIVE) {
                                enableSession();
                            }
                            else {
                                disableSession();
                                //showSessionEndedNotification();
                            }
                        }
                        else {
                            disableSession();
                        }
                    }
                });
    }

    private void showSessionActiveNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "RES")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Session Active")
                .setContentText("Restaurant name + table")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int sessionActiveId = 1001;
        notificationManager.notify(sessionActiveId, builder.build());
    }

    private void showSessionEndedNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "RES")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Session Ended")
                .setContentText("Bill Details? Rating?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int sessionEndedId = 1002;
        notificationManager.notify(sessionEndedId, builder.build());
    }

    private void getSessionDetails() {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        if (user == null)
            return;

        DocumentReference userRef = firebaseFirestore
                .collection("users")
                .document(user.getUid());

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
                                    restaurantName.setText(doc1.getString("name"));
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
                                                            tableName.setText(tableSerial);
                                                        }
                                                        bottomSheet.setVisibility(View.VISIBLE);
                                                        resumeSession.putExtra("restaurant_id", restaurant);
                                                        resumeSession.putExtra("restaurant_name", restaurantName.getText().toString());
                                                        resumeSession.putExtra("table_number", tableSerial);
                                                        mBottomNavOrderButton.setEnabled(true);
                                                        //showSessionActiveNotification();
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
                .document(user.getUid());

        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("Failed", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                boolean sessionActive = (boolean) snapshot.get("session_active");
                
                if (sessionActive) {
                    enableSession();
                    showSessionActiveNotification();
                }
                else if (SESSION_ACTIVE){
                    disableSession();
                    showSessionEndedNotification();
                }
                
                Log.d("Snapshot Exists", "Current data: " + snapshot.getData());
            }
            else {
                Log.d("No Snapshot", "Current data: null");
            }
        });
    }

    private void enableSession() {
        restaurantName = findViewById(R.id.text_restaurant);
        tableName = findViewById(R.id.text_table);

        getSessionDetails();

        viewMenuButton = findViewById(R.id.button_view_menu);
        viewMenuButton.setOnClickListener(v -> startActivity(resumeSession));
    }

    private void disableSession() {
        SESSION_ACTIVE = false;
        bottomSheet.setVisibility(View.GONE);
        mBottomNavOrderButton.setEnabled(true);
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
