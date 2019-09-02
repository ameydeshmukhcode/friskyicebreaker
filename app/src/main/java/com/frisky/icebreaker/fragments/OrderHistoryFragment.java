package com.frisky.icebreaker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.VisitsAdapter;
import com.google.android.material.tabs.TabLayout;

public class OrderHistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        ViewPager pager = view.findViewById(R.id.pager_visits);
        TabLayout tabs = view.findViewById(R.id.tabs_visits);

        tabs.setupWithViewPager(pager);
        pager.setAdapter(new VisitsAdapter(getActivity()));

        return view;
    }
}
