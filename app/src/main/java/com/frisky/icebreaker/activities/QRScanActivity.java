package com.frisky.icebreaker.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.frisky.icebreaker.BuildConfig;
import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.components.dialogs.ConfirmSessionStartDialog;
import com.frisky.icebreaker.ui.components.dialogs.ProgressDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QRScanActivity extends AppCompatActivity implements ConfirmSessionStartDialog.OnConfirmSessionStart {

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    FrameLayout mScanner;
    CodeScanner mCodeScanner;
    ConstraintLayout mDummyMenu;

    SharedPreferences sharedPreferences;
    FirebaseUser mUser;

    String restaurantID;
    String tableID;
    String qrCodeData;

    ProgressDialog progressDialog = new ProgressDialog("Retrieving the Menu");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mScanner = findViewById(R.id.frame_scanner);
        mDummyMenu = findViewById(R.id.dummy_menu);
        mDummyMenu.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            setupScannerView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCodeScanner != null) mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        if (mCodeScanner != null) mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupScannerView();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public void sessionStart(boolean choice) {
        if (choice) {
            progressDialog.show(getSupportFragmentManager(), "progress dialog");
            progressDialog.setCancelable(false);
            mDummyMenu.setVisibility(View.VISIBLE);
            mCodeScanner.stopPreview();
            mCodeScanner.releaseResources();
            mScanner.setVisibility(View.GONE);
            continueSessionStart();
        } else {
            mCodeScanner.startPreview();
        }
    }

    private void setupScannerView() {
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            qrCodeData = result.getText();

            ConfirmSessionStartDialog confirmSessionStartDialog = new ConfirmSessionStartDialog();
            confirmSessionStartDialog.show(getSupportFragmentManager(), "confirm session start dialog");
        }));
        mCodeScanner.startPreview();
    }

    private void continueSessionStart() {
        if (!qrCodeData.contains("frisky") || (qrCodeData.split("\\+").length != 3)) {
            Toast.makeText(getBaseContext(),"Invalid QR Code", Toast.LENGTH_LONG).show();
            updateOnSessionStartFail();
            mCodeScanner.startPreview();
        } else {
            restaurantID = qrCodeData.split("\\+")[1];
            tableID = qrCodeData.split("\\+")[2];

            if (BuildConfig.DEBUG) {
                DocumentReference restaurantRef = FirebaseFirestore.getInstance()
                        .collection("restaurants")
                        .document(restaurantID);

                restaurantRef.get().addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       DocumentSnapshot document = task.getResult();
                       if (document == null) return;
                       if (document.exists()) {
                           if (Objects.equals(document.getString("status_listing"), "debug")) {
                               checkForTableOccupied();
                           } else {
                               Toast.makeText(getBaseContext(),"Invalid QR Code", Toast.LENGTH_LONG).show();
                               updateOnSessionStartFail();
                           }
                       }
                   }
                });
            } else {
                checkForTableOccupied();
            }
        }
    }

    private void checkForTableOccupied() {
        DocumentReference tableRef = FirebaseFirestore.getInstance()
                .collection("restaurants")
                .document(restaurantID)
                .collection("tables")
                .document(tableID);

        tableRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null) return;
                if (document.exists()) {
                    boolean isOccupied = false;
                    if (document.contains("occupied")) {
                        isOccupied = (boolean) document.get("occupied");
                    }
                    if (isOccupied) {
                        Toast.makeText(getBaseContext(),"Table is Occupied", Toast.LENGTH_LONG).show();
                        updateOnSessionStartFail();
                        mCodeScanner.startPreview();
                    } else {
                        getRestaurantAndTableDetails(restaurantID, tableID);
                        initUserSession(restaurantID, tableID);
                    }
                } else {
                    Toast.makeText(getBaseContext(),"Invalid QR Code", Toast.LENGTH_LONG).show();
                    updateOnSessionStartFail();
                    mCodeScanner.startPreview();
                    Log.e(getString(R.string.tag_debug), "No such document");
                }
            } else {
                Log.e(getString(R.string.tag_debug), "failed with ", task.getException());
            }
        });
    }

    private void getRestaurantAndTableDetails(String restID, String tableID) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference resRef = firebaseFirestore.collection("restaurants").document(restID);
        resRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null)
                    return;
                if (document.exists()) {
                    String restaurantName = document.getString("name");
                    sharedPreferences.edit().putString("restaurant_name", restaurantName).apply();
                    Log.d(getString(R.string.tag_debug), "DocumentSnapshot data: " + document.getData());
                }
                else {
                    Log.e("Doesn't exist", "No such document");
                }
            }
            else {
                Log.e("Task", "failed with ", task.getException());
            }
        }).addOnFailureListener(e -> updateOnSessionStartFail());
        DocumentReference tableRef = firebaseFirestore
                .collection("restaurants")
                .document(restID)
                .collection("tables")
                .document(tableID);
        tableRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null)
                    return;
                if (document.exists()) {
                    String tableSerial = "Table " + document.get("number");
                    sharedPreferences.edit().putString("table_name", tableSerial).apply();
                    Log.d(getString(R.string.tag_debug), "DocumentSnapshot data: " + document.getData());
                }
                else {
                    Log.e("Doesn't exist", "No such document");
                }
            }
            else {
                Log.e("Task", "failed with ", task.getException());
            }
        }).addOnFailureListener(e -> updateOnSessionStartFail());
    }

    private void initUserSession(final String restID, final String tableID) {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("table_id", tableID);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            sessionDetails.put("created_by", FirebaseAuth.getInstance().getCurrentUser().getUid());
        sessionDetails.put("start_time", new Timestamp(System.currentTimeMillis()));
        sessionDetails.put("is_active", true);
        firebaseFirestore.collection("restaurants")
                .document(restID)
                .collection("sessions")
                .add(sessionDetails)
                .addOnSuccessListener(documentReference -> {
                    final String sessionID = documentReference.getId();
                    Log.d(getString(R.string.tag_debug), "DocumentSnapshot written with ID: " + documentReference.getId());
                    Map<String, Object> userSessionDetails = new HashMap<>();
                    userSessionDetails.put("session_active", true);
                    userSessionDetails.put("current_session", sessionID);
                    userSessionDetails.put("restaurant", restID);
                    sharedPreferences.edit()
                            .putBoolean("session_active", true)
                            .putString("restaurant_id", restID)
                            .putString("session_id", sessionID)
                            .putString("table_id", tableID)
                            .apply();
                    firebaseFirestore.collection("users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .set(userSessionDetails, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Map<String, Object> tableSessionDetails = new HashMap<>();
                                tableSessionDetails.put("occupied", true);
                                tableSessionDetails.put("session_id", sessionID);
                                firebaseFirestore.collection("restaurants")
                                        .document(restID)
                                        .collection("tables")
                                        .document(tableID)
                                        .set(tableSessionDetails, SetOptions.merge())
                                        .addOnSuccessListener(aVoid1 -> showMenu())
                                        .addOnFailureListener(e -> updateOnSessionStartFail());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("", "Error adding document", e);
                    updateOnSessionStartFail();
                });
    }

    private void showMenu() {
        Intent showMenu = new Intent(getBaseContext(), MenuActivity.class);
        startActivity(showMenu);
        finish();
    }

    private void updateOnSessionStartFail() {
        mDummyMenu.setVisibility(View.GONE);
        mScanner.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
    }
}
