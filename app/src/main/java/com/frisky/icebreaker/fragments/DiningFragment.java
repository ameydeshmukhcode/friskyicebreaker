package com.frisky.icebreaker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.activities.QRScanActivity;

public class DiningFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining_empty_state, container, false);

        Button scanQRCodeButton = view.findViewById(R.id.button_scan_qr);
        scanQRCodeButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), QRScanActivity.class));
        });

        return view;
    }
}
