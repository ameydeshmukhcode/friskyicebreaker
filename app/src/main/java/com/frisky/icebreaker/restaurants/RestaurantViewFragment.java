package com.frisky.icebreaker.restaurants;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.frisky.icebreaker.orders.OrderingAssistant.SESSION_ACTIVE;

public class RestaurantViewFragment extends Fragment {

    private List<Restaurant> restaurantList = new ArrayList<>();

    private RecyclerView.Adapter mPubViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_restaurant, null);

        RecyclerView mRecyclerPubView;
        mRecyclerPubView = view.findViewById(R.id.recycler_view);

        if (SESSION_ACTIVE) {
            mRecyclerPubView.setPadding(0, 0, 0, 0);
            mRecyclerPubView.setPadding(0, 0, 0, 225);
            mRecyclerPubView.setClipToPadding(false);
        }

        RecyclerView.LayoutManager mPubViewLayoutManager;
        // use a linear layout manager
        mPubViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerPubView.setLayoutManager(mPubViewLayoutManager);

        // specify an adapter (see also next example)
        mPubViewAdapter = new RestaurantListAdapter(restaurantList, getContext());
        mRecyclerPubView.setAdapter(mPubViewAdapter);

        preparePubData();

        return view;
    }

    private void preparePubData() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String image = document.get("image").toString();
                                String name = document.get("name").toString();
                                String address = document.get("address").toString();
                                String tags = document.get("cuisine").toString();
                                Log.i("Rest", name + " " + address + " " + tags);
                                Restaurant restaurant = new Restaurant(Uri.parse(image), document.getId(), name, name, address,
                                        Arrays.asList(tags.substring(1, tags.length()-1)), 4.5);
                                restaurantList.add(restaurant);
                                mPubViewAdapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            Log.e("error", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
