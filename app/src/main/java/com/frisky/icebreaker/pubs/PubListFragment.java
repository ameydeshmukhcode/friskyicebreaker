package com.frisky.icebreaker.pubs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.Pub;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.get("name").toString();
                                String address = document.get("address").toString();
                                String tags = document.get("cuisine").toString();
                                Log.i("Rest", name + " " + address + " " + tags);
                                Pub pub = new Pub(document.getId(), name, name, address,
                                        Arrays.asList(tags.substring(1, tags.length()-1)), 4.5);
                                pubList.add(pub);
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
