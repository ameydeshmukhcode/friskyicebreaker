package com.frisky.icebreaker.orders;

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
import android.widget.Toast;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.User;
import com.frisky.icebreaker.core.structures.menu.MenuItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuItemListFragment extends Fragment {

    private String restID;
    private List<MenuItem> menu = new ArrayList<>();

    private RecyclerView.Adapter mMenuListViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_recycler_view, null);

        RecyclerView mRecyclerMenuListView;
        mRecyclerMenuListView = view.findViewById(R.id.recycler_view);
        mRecyclerMenuListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        restID = getArguments().getString("restaurant_id");

        RecyclerView.LayoutManager mMenuListViewLayoutManager;
        // use a linear layout manager
        mMenuListViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerMenuListView.setLayoutManager(mMenuListViewLayoutManager);

        // specify an adapter (see also next example)
        mMenuListViewAdapter = new MenuItemListAdapter(menu);
        mRecyclerMenuListView.setAdapter(mMenuListViewAdapter);

        prepareMenuData();

        return view;
    }

    private void prepareMenuData() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        Query itemsListRef = mFirestore.collection("restaurants").document(restID)
                .collection("items").orderBy("categoryid");

        itemsListRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Item", document.getId() + " => " + document.getData());
                        String name = document.getString("name");
                        String cost = document.getString("cost");
                        MenuItem item = new MenuItem(name, name, Integer.parseInt(cost));
                        menu.add(item);
                        mMenuListViewAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    Log.e("error", "Error getting documents: ", task.getException());
                }
            }
        });

    }
}
