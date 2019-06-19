package com.frisky.icebreaker.orders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.frisky.icebreaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

public class QRScanActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

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
                                           String permissions[], @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            Log.i("Camera", "G : " + grantResults[0]);
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupScannerView();
            }
            else {
                super.onBackPressed();
            }
        }
    }

    private void setupScannerView() {
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        final String qrCodeData = result.getText();

                        if (!qrCodeData.contains("frisky")) {
                            Toast.makeText(getBaseContext(),"QR Code not recognised", Toast.LENGTH_LONG).show();
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

                            tableRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document == null)
                                            return;
                                        if (document.exists()) {
                                            boolean isOccupied = false;
                                            if (document.contains("occupied")) {
                                                isOccupied = Boolean.parseBoolean(document.get("occupied").toString());
                                            }
                                            if (!isOccupied) {
                                                Intent showMenu = new Intent(getBaseContext(), MenuActivity.class);
                                                showMenu.putExtra("qr_code_scanned", true);
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
                                }
                            });
                        }
                    }
                });
            }
        });
        mCodeScanner.startPreview();
    }
}
