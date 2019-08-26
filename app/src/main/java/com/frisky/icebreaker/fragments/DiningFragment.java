package com.frisky.icebreaker.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.activities.OrderActivity;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class DiningFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity())
                .getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        if (sharedPreferences.getBoolean("session_active", false)) {
            if (sharedPreferences.getBoolean("bill_requested", false)) {
                view = inflater.inflate(R.layout.fragment_dining_bill_requested, container, false);
            }
            else {
                view = inflater.inflate(R.layout.fragment_dining_session_active, container, false);
                TextView restaurant = view.findViewById(R.id.text_pub_name);
                TextView table = view.findViewById(R.id.text_table);
                restaurant.setText(sharedPreferences.getString("restaurant_name", ""));
                table.setText(sharedPreferences.getString("table_name", ""));
                Button showOrders = view.findViewById(R.id.button_show_orders);
                showOrders.setOnClickListener(v -> {
                    if (sharedPreferences.contains("order_active")) {
                        Toast toast = Toast.makeText(getActivity(), "No orders placed yet!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    else {
                        startActivity(new Intent(getActivity(), OrderActivity.class));
                    }
                });
            }
            return view;
        }
        else {
            view = inflater.inflate(R.layout.fragment_dining_scan_qr, container, false);
            return view;
        }
    }
}
