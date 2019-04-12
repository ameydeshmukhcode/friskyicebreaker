package com.frisky.icebreaker.ui.pubview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.frisky.icebreaker.ui.components.bottomsheet.filters.BottomSheetFragment;
import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.Pub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PubViewFragment extends Fragment {

    private ImageButton mFiltersButton;
    private List<Pub> pubList = new ArrayList<>();

    private RecyclerView mRecyclerPubView;
    private RecyclerView.Adapter mPubViewAdapter;
    private RecyclerView.LayoutManager mPubViewLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_pub_view, null);

        mFiltersButton = view.findViewById(R.id.button_filters);
        mFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogFragment();
            }
        });

        mRecyclerPubView = view.findViewById(R.id.recycler_pub_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerPubView.setHasFixedSize(true);

        // use a linear layout manager
        mPubViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerPubView.setLayoutManager(mPubViewLayoutManager);

        // specify an adapter (see also next example)
        mPubViewAdapter = new PubViewAdapter(pubList, getContext());
        mRecyclerPubView.setAdapter(mPubViewAdapter);

        preparePubData();

        return view;
    }

    public void showBottomSheetDialogFragment() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
    }

    private void preparePubData() {
        Pub pub = new Pub("DJ the BJ", "DJ the BJ", Arrays.asList("Alcohol", "Food"), 4.5);
        pubList.add(pub);

        pub = new Pub("Joey's Roaches", "Joey's Roaches", Arrays.asList("Alcohol", "Food"), 3.7);
        pubList.add(pub);

        pub = new Pub("Biergarten", "Biergarten", Arrays.asList("Alcohol", "Food"), 2.2);
        pubList.add(pub);

        pub = new Pub("Gourmet Theatre", "Gourmet Theatre", Arrays.asList("Alcohol", "Food"), 3.2);
        pubList.add(pub);

        pub = new Pub("Rasta Cafe", "Rasta Cafe", Arrays.asList("Alcohol", "Food"), 1.0);
        pubList.add(pub);

        pub = new Pub("Joey's Roaches", "Joey's Roaches", Arrays.asList("Alcohol", "Food"), 4.0);
        pubList.add(pub);

        pub = new Pub("Biergarten", "Biergarten", Arrays.asList("Alcohol", "Food"), 2.6);
        pubList.add(pub);

        mPubViewAdapter.notifyDataSetChanged();
    }
}
