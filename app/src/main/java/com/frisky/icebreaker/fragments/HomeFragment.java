package com.frisky.icebreaker.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.frisky.icebreaker.BuildConfig;
import com.frisky.icebreaker.R;
import com.frisky.icebreaker.activities.OptionsActivity;
import com.frisky.icebreaker.adapters.RestaurantListAdapter;
import com.frisky.icebreaker.core.structures.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private RecyclerView.Adapter mPubViewAdapter;

    private ShimmerFrameLayout mShimmerViewContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_home, viewGroup, false);

        Button settingsButton = view.findViewById(R.id.button_settings);
        settingsButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), OptionsActivity.class)));

        mShimmerViewContainer = view.findViewById(R.id.shimmer_list);

        RecyclerView mRecyclerPubView = view.findViewById(R.id.recycler_view);

        mRecyclerPubView.setPadding(0, 0, 0, 0);
        mRecyclerPubView.setPadding(0, 0, 0, 225);
        mRecyclerPubView.setClipToPadding(false);

        RecyclerView.LayoutManager mPubViewLayoutManager;
        // use a linear layout manager
        mPubViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerPubView.setLayoutManager(mPubViewLayoutManager);

        // specify an adapter (see also next example)
        mPubViewAdapter = new RestaurantListAdapter(mRestaurantList, getContext());
        mRecyclerPubView.setAdapter(mPubViewAdapter);

        preparePubData();

        return view;
    }

    private void preparePubData() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        if (BuildConfig.DEBUG) {
            mFirestore.collection("restaurants")
                    .whereEqualTo("status_listing", "debug")
                    .get()
                    .addOnCompleteListener(this::addToRestaurantList);
        }
        else {
            mFirestore.collection("restaurants")
                    .whereEqualTo("status_listing", "complete")
                    .get()
                    .addOnCompleteListener(this::addToRestaurantList);
        }
    }

    private void addToRestaurantList(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            if (task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String image = "";
                    String name = "";
                    String address = "";
                    String tags = "";
                    if (document.contains("image")) {
                        image = document.getString("image");
                    }
                    if (document.contains("name")) {
                        name = document.getString("name");
                    }
                    if (document.contains("address")) {
                        address = document.getString("address");
                    }
                    if (document.contains("cuisine")) {
                        tags = Objects.requireNonNull(document.get("cuisine")).toString();
                    }

                    Log.d("Frisky Debug", name + " " + address + " " + tags);

                    Restaurant restaurant = new Restaurant(Uri.parse(image), document.getId(), name, address,
                            tags.substring(1, tags.length() - 1), 4.5);
                    mRestaurantList.add(restaurant);
                    mPubViewAdapter.notifyDataSetChanged();
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                }
            }
        }
        else {
            Log.e("error", "Error getting documents: ", task.getException());
        }
    }
}
