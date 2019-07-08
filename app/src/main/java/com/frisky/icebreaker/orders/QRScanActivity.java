package com.frisky.icebreaker.orders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.frisky.icebreaker.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.frisky.icebreaker.orders.OrderingAssistant.SESSION_ACTIVE;

public class QRScanActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

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
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            final String qrCodeData = result.getText();

            if (!qrCodeData.contains("frisky")) {
                Toast.makeText(getBaseContext(),"QR Code not recognised", Toast.LENGTH_LONG).show();
                mCodeScanner.startPreview();
            }
            else if (SESSION_ACTIVE) {
                Toast.makeText(getBaseContext(),"Session Already Active", Toast.LENGTH_LONG).show();
                mCodeScanner.startPreview();
            }
            else {
                final String restaurant = qrCodeData.split("\\+")[1];
                final String table = qrCodeData.split("\\+")[2];

                DocumentReference tableRef = firebaseFirestore
                        .collection("restaurants")
                        .document(restaurant)
                        .collection("tables")
                        .document(table);

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
                            if (!isOccupied) {
                                Intent showMenu = new Intent(getBaseContext(), MenuActivity.class);
                                showMenu.putExtra("start_new_session", true);
                                showMenu.putExtra("restaurant_id", restaurant);
                                showMenu.putExtra("table_id", table);
                                startActivity(showMenu);
                                finish();
                            }
                            else {
                                Toast.makeText(getBaseContext(),"Table is Occupied", Toast.LENGTH_LONG).show();
                                mCodeScanner.startPreview();
                            }
                            Log.i("Exists", "DocumentSnapshot data: " + document.getData());
                        }
                        else {
                            Log.e("Doesn't exist", "No such document");
                        }
                    }
                    else {
                        Log.e("Task", "failed with ", task.getException());
                    }
                });
            }
        }));
        mCodeScanner.startPreview();
    }
}
