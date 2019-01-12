package com.frisky.icebreaker.ui.components.bottomsheet.filters;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.pubview.PubViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private List<String> filtersList = new ArrayList<>();

    private RecyclerView mRecyclerFiltersView;
    private RecyclerView.Adapter mFiltersViewAdapter;
    private RecyclerView.LayoutManager mFiltersViewLayoutManager;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view;
        view = inflater.inflate(R.layout.bottom_sheet_filters, null);

        mRecyclerFiltersView = view.findViewById(R.id.recycler_filters);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerFiltersView.setHasFixedSize(true);

        // use a linear layout manager
        mFiltersViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerFiltersView.setLayoutManager(mFiltersViewLayoutManager);

        // specify an adapter (see also next example)
        mFiltersViewAdapter = new BottomSheetFiltersViewAdapter(filtersList);
        mRecyclerFiltersView.setAdapter(mFiltersViewAdapter);

        prepareFilters();

        return view;
    }

    private void prepareFilters() {
        String filter = new String("Alcohol");
        filtersList.add(filter);

        filter = new String("Food");
        filtersList.add(filter);

        filter = new String("Veg Only");
        filtersList.add(filter);

        filter = new String("Drinks Only");
        filtersList.add(filter);

        filter = new String("Dance");
        filtersList.add(filter);

        filter = new String("Hookah");
        filtersList.add(filter);

        filter = new String("Night");
        filtersList.add(filter);
    }
}
