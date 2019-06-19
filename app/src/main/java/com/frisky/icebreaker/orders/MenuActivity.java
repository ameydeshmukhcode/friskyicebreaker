package com.frisky.icebreaker.orders;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity implements UIActivity {

    ImageButton mBackButton;
    TextView mRestName;
    TextView mTableSerial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("qr_code_scanned")){
            setContentView(R.layout.activity_menu);
        }
        else {
            setContentView(R.layout.activity_menu_empty_state);
        }

        initUI();
    }

    @Override
    public void initUI() {
        mBackButton = findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuActivity.super.onBackPressed();
            }
        });

        if (getIntent().hasExtra("qr_code_scanned")
            && getIntent().hasExtra("table_id")
            && getIntent().hasExtra("restaurant_id")){

            final String restID = getIntent().getStringExtra("restaurant_id");;
            final String tableID = getIntent().getStringExtra("table_id");;

            mRestName = findViewById(R.id.text_pub_name);
            mTableSerial = findViewById(R.id.text_table);

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            DocumentReference userRef = firebaseFirestore.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document == null)
                            return;
                        if (document.exists()) {
                            boolean isSessionActive = false;
                            if (document.contains("sessionactive")) {
                                isSessionActive = Boolean.parseBoolean(document.get("sessionactive").toString());
                            }
                            if (!isSessionActive) {
                                initUserSession(restID, tableID);
                                getRestaurantAndTableDetails(restID, tableID);
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

            MenuFragment menuFragment = new MenuFragment();

            Bundle bundle = new Bundle();
            bundle.putString("restaurant_id", restID);
            menuFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_menu, menuFragment)
                    .commit();
        }
    }

    private void initUserSession(final String restID, String tableID) {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("tableid", tableID);
        data.put("createdby", FirebaseAuth.getInstance().getCurrentUser().getUid());
        data.put("starttime", new Timestamp(System.currentTimeMillis()));
        data.put("isactive", true);

        firebaseFirestore.collection("restaurants")
                .document(restID)
                .collection("sessions")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String id = documentReference.getId();
                        Log.e("", "DocumentSnapshot written with ID: " + documentReference.getId());

                        Map<String, Object> data = new HashMap<>();
                        data.put("sessionactive", true);
                        data.put("currentsession", id);
                        data.put("restaurant", restID);

                        firebaseFirestore.collection("users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .set(data, SetOptions.merge());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("", "Error adding document", e);
                    }
                });
    }

    private void getRestaurantAndTableDetails(String restID, String tableID) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference resRef = firebaseFirestore.collection("restaurants").document(restID);
        resRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document == null)
                        return;
                    if (document.exists()) {
                        mRestName.setText(document.get("name").toString());
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

        DocumentReference tableRef = firebaseFirestore
                .collection("restaurants")
                .document(restID)
                .collection("tables")
                .document(tableID);

        tableRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document == null)
                        return;
                    if (document.exists()) {
                        String tableSerial = "Table " + document.get("number");
                        mTableSerial.setText(tableSerial);
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
