package com.frisky.icebreaker.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frisky.icebreaker.R;

import static android.content.Context.MODE_PRIVATE;

public class DiningFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        if (sharedPreferences.getBoolean("session_active", false)) {
            if (sharedPreferences.getBoolean("bill_requested", false)) {
                view = inflater.inflate(R.layout.fragment_dining_bill_requested, container, false);
            }
            else {
                view = inflater.inflate(R.layout.fragment_dining_session_active, container, false);
            }
            return view;
        }
        else {
            view = inflater.inflate(R.layout.fragment_dining_scan_qr, container, false);
            return view;
        }
    }
}
