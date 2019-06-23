package com.frisky.icebreaker.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.frisky.icebreaker.orders.OrderingAssistant.SESSION_ACTIVE;

public class MenuActivity extends AppCompatActivity implements UIActivity {

    ImageButton mBackButton;
    TextView mRestName;
    TextView mTableSerial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SESSION_ACTIVE || getIntent().hasExtra("start_new_session")) {
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

        if (SESSION_ACTIVE) {
            mRestName = findViewById(R.id.text_pub_name);
            mTableSerial = findViewById(R.id.text_table);

            restoreUserSession();
        } else if (getIntent().hasExtra("start_new_session")) {
            if (getIntent().hasExtra("table_id")
                    && getIntent().hasExtra("restaurant_id")) {

                mRestName = findViewById(R.id.text_pub_name);
                mTableSerial = findViewById(R.id.text_table);

                final String restID = getIntent().getStringExtra("restaurant_id");
                final String tableID = getIntent().getStringExtra("table_id");

                getRestaurantAndTableDetails(restID, tableID);
                initUserSession(restID, tableID);
                sendRestaurantID(restID);
            }
        }
    }

    private void sendRestaurantID(String restaurant) {
        MenuFragment menuFragment = new MenuFragment();

        Bundle bundle = new Bundle();
        bundle.putString("restaurant_id", restaurant);
        menuFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_menu, menuFragment)
                .commit();
    }

    private void initUserSession(final String restID, final String tableID) {
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
                        final String sessionID = documentReference.getId();
                        Log.e("", "DocumentSnapshot written with ID: " + documentReference.getId());

                        Map<String, Object> data = new HashMap<>();
                        data.put("sessionactive", true);
                        data.put("currentsession", sessionID);
                        data.put("restaurant", restID);

                        SESSION_ACTIVE = true;

                        firebaseFirestore.collection("users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .set(data, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("occupied", true);
                                        data.put("sessionid", sessionID);

                                        firebaseFirestore.collection("restaurants")
                                                .document(restID)
                                                .collection("tables")
                                                .document(tableID)
                                                .set(data, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.i("Success", "Table details updated");
                                                    }
                                                });
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("", "Error adding document", e);
                    }
                });
    }

    private void restoreUserSession() {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference userRef = firebaseFirestore
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.contains("sessionactive")) {
                                boolean isSessionActive = (boolean) doc.get("sessionactive");
                                if (isSessionActive) {
                                    if (doc.contains("restaurant") && doc.contains("currentsession")) {
                                        final String restaurant = doc.getString("restaurant");
                                        final String currentSession = doc.getString("currentsession");

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
                                                                mRestName.setText(doc.getString("name"));
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
                                                                            if (doc.contains("tableid")) {
                                                                                String tableid = doc.getString("tableid");

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
                                                                                                    if (doc.contains("number")) {
                                                                                                        String tableSerial = "Table " + doc.get("number");
                                                                                                        mTableSerial.setText(tableSerial);
                                                                                                    }
                                                                                                }
                                                                                                sendRestaurantID(restaurant);
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
                        }
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
