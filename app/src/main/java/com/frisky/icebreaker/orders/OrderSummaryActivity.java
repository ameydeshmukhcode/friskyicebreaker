package com.frisky.icebreaker.orders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UIActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class OrderSummaryActivity extends AppCompatActivity implements UIActivity {

    TextView mRestaurantName;
    TextView mTableName;
    TextView mOrderDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        initUI();
    }

    @Override
    public void initUI() {
        mRestaurantName = findViewById(R.id.text_restaurant);
        mTableName = findViewById(R.id.text_table);
        mOrderDateTime = findViewById(R.id.text_date_time);

        if (getIntent().hasExtra("restaurant_name")) {
            mRestaurantName.setText(getIntent().getStringExtra("restaurant_name"));
        }

        if (getIntent().hasExtra("table_name")) {
            mTableName.setText(getIntent().getStringExtra("table_name"));
        }

        if (getIntent().hasExtra("session_id") && getIntent().hasExtra("restaurant_id")) {
            getOrderSummary(getIntent().getStringExtra("restaurant_id"),
                    getIntent().getStringExtra("session_id"));
            Log.d(getString(R.string.tag_debug), getIntent().getStringExtra("restaurant_id") +
                    getIntent().getStringExtra("session_id"));
        }
    }

    private void getOrderSummary(String restaurant, String session) {

        FirebaseFirestore.getInstance().collection("restaurants").document(restaurant)
                .collection("sessions").document(session)
                .get().addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       DocumentSnapshot snapshot = task.getResult();
                       if (snapshot == null)
                           return;
                       if (snapshot.contains("end_time")) {
                           @SuppressLint("SimpleDateFormat")
                           SimpleDateFormat formatter = new SimpleDateFormat("dd MMM YYYY hh:mm a");
                           String endTime = formatter.format((Objects
                                   .requireNonNull(snapshot.getTimestamp("end_time")).toDate()));
                           mOrderDateTime.setText(endTime);
                       }
                   }
                });
    }
}
