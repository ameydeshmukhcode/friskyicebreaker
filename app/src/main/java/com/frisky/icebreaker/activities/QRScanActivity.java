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
import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.components.dialogs.ConfirmSessionStartDialog;
import com.frisky.icebreaker.ui.components.dialogs.ProgressDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class QRScanActivity extends AppCompatActivity implements ConfirmSessionStartDialog.OnConfirmSessionStart {

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    FrameLayout mScanner;
    CodeScanner mCodeScanner;
    ConstraintLayout mDummyMenu;

    SharedPreferences sharedPreferences;

    String restaurantID;
    String tableID;
    String qrCodeData;

    ProgressDialog progressDialog = new ProgressDialog("Retrieving the Menu");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        mScanner = findViewById(R.id.frame_scanner);
        mDummyMenu = findViewById(R.id.dummy_menu);
        mDummyMenu.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            setupScannerView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCodeScanner != null)
            mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        if (mCodeScanner != null)
            mCodeScanner.releaseResources();
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
                }
                else {
                    super.onBackPressed();
                }
            }
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
        }
        else {
            mCodeScanner.startPreview();
        }
    }

    private void continueSessionStart() {
        if (!qrCodeData.contains("frisky") || (qrCodeData.split("\\+").length != 3)) {
            Toast.makeText(getBaseContext(),"Invalid QR Code", Toast.LENGTH_LONG).show();
            updateOnSessionStartFail();
            mCodeScanner.startPreview();
            return;
        }
        else {
            restaurantID = qrCodeData.split("\\+")[1];
            tableID = qrCodeData.split("\\+")[2];
        }

        DocumentReference restaurantRef = FirebaseFirestore.getInstance()
                .collection("restaurants")
                .document(restaurantID);

        DocumentReference tableRef = restaurantRef.collection("tables")
                .document(tableID);

        tableRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null)
                    return;
                if (document.exists()) {
                    boolean isOccupied = false;
                    if (document.contains("occupied")) {
                        isOccupied = (boolean) document.get("occupied");
                    }
                    if (isOccupied) {
                        Toast.makeText(getBaseContext(),"Table is Occupied", Toast.LENGTH_LONG).show();
                        updateOnSessionStartFail();
                        mCodeScanner.startPreview();
                    }
                    else {
                        createUserSession();
                    }
                    Log.d(getString(R.string.tag_debug), "DocumentSnapshot data: " + document.getData());
                }
                else {
                    Toast.makeText(getBaseContext(),"Invalid QR Code", Toast.LENGTH_LONG).show();
                    updateOnSessionStartFail();
                    mCodeScanner.startPreview();
                    Log.e(getString(R.string.tag_debug), "No such document");
                }
            }
            else {
                Log.e(getString(R.string.tag_debug), "failed with ", task.getException());
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void createUserSession() {
        Map<String, Object> data = new HashMap<>();
        data.put("restaurant", restaurantID);
        data.put("table", tableID);

        FirebaseFunctions.getInstance()
                .getHttpsCallable("createUserSession")
                .call(data)
                .continueWith(task -> {
                    if(task.isSuccessful()) {
                        if (task.getResult() != null) {
                            Map<String, Object> resultData = (Map<String, Object>) task.getResult().getData();
                            if (resultData != null) {
                                String restaurantName = (String) resultData.get("restaurant_name");
                                String tableName = (String) resultData.get("table_name");
                                String sessionID = (String) resultData.get("session_id");

                                showMenu(restaurantName, tableName, sessionID);
                            }
                        }
                    }
                    else if (!task.isSuccessful()){
                        updateOnSessionStartFail();
                        Toast.makeText(getBaseContext(), "Something went wrong :( Try again.", Toast.LENGTH_SHORT).show();
                    }

                    return "createUserSession";
                });
    }

    private void showMenu(String restaurantName, String tableName, String sessionID) {
        sharedPreferences.edit()
                .putBoolean("session_active", true)
                .putString("session_id", sessionID)
                .putString("restaurant_id", restaurantID)
                .putString("restaurant_name", restaurantName)
                .putString("table_id", tableID)
                .putString("table_name", tableName)
                .apply();

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
