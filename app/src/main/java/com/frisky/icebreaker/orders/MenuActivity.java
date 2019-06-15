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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuActivity extends AppCompatActivity implements UIActivity {

    ImageButton mBackButton;
    TextView mRestName;

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

        if (getIntent().hasExtra("qr_code_scanned")){
            String restID = "";

            if (getIntent().hasExtra("restaurant_id")) {
                restID = getIntent().getStringExtra("restaurant_id");
            }

            mRestName = findViewById(R.id.text_pub_name);

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            DocumentReference docRef = firebaseFirestore.collection("restaurants").document(restID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

            MenuFragment menuFragment = new MenuFragment();

            Bundle bundle = new Bundle();
            bundle.putString("restaurant_id", restID);
            menuFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_menu, menuFragment)
                    .commit();
        }
    }
}
