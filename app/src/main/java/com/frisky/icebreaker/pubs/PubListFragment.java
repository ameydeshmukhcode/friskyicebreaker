package com.frisky.icebreaker.pubs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.Pub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PubListFragment extends Fragment {

    private List<Pub> pubList = new ArrayList<>();

    private RecyclerView.Adapter mPubViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_recycler_view, null);

        RecyclerView mRecyclerPubView;
        mRecyclerPubView = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mPubViewLayoutManager;
        // use a linear layout manager
        mPubViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerPubView.setLayoutManager(mPubViewLayoutManager);

        // specify an adapter (see also next example)
        mPubViewAdapter = new PubListAdapter(pubList, getContext());
        mRecyclerPubView.setAdapter(mPubViewAdapter);

        preparePubData();

        return view;
    }
    
    private void preparePubData() {
        Pub pub = new Pub("DJ the BJ", "DJ the BJ", "Idhar",
                Arrays.asList("Alcohol", "Food"), 4.5);
        pubList.add(pub);

        pub = new Pub("Joey's Roaches", "Joey's Roaches", "Udhar",
                Arrays.asList("Alcohol", "Food"), 3.7);
        pubList.add(pub);

        pub = new Pub("Biergarten", "Biergarten", "Mera Ghar",
                Arrays.asList("Alcohol", "Food"), 2.2);
        pubList.add(pub);

        pub = new Pub("Gourmet Theatre", "Gourmet Theatre", "Arre Wah",
                Arrays.asList("Alcohol", "Food"), 3.2);
        pubList.add(pub);

        pub = new Pub("Rasta Cafe", "Rasta Cafe", "Kidhar?",
                Arrays.asList("Alcohol", "Food"), 1.0);
        pubList.add(pub);

        pub = new Pub("Naya Naam", "Naya Naam", "Bada Gaon",
                Arrays.asList("Alcohol", "Food"), 4.0);
        pubList.add(pub);

        pub = new Pub("Kya Pata", "Kya Pata", "Nice",
                Arrays.asList("Alcohol", "Food"), 2.6);
        pubList.add(pub);

        mPubViewAdapter.notifyDataSetChanged();
    }
}
