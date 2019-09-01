package com.frisky.icebreaker.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.activities.OptionsActivity;
import com.frisky.icebreaker.adapters.RestaurantListAdapter;
import com.frisky.icebreaker.core.structures.Restaurant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantViewFragment extends Fragment {

    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private RecyclerView.Adapter mPubViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_restaurant, viewGroup, false);

        Button settingsButton = view.findViewById(R.id.button_settings);
        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), OptionsActivity.class));
        });

        RecyclerView mRecyclerPubView;
        mRecyclerPubView = view.findViewById(R.id.recycler_view);

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

        mFirestore.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
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

                                Restaurant restaurant = new Restaurant(Uri.parse(image), document.getId(), name, name, address,
                                        tags.substring(1, tags.length() - 1), 4.5);
                                mRestaurantList.add(restaurant);

                                mPubViewAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    else {
                        Log.e("error", "Error getting documents: ", task.getException());
                    }
                });
    }
}
