package com.frisky.icebreaker.orders;

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
import com.frisky.icebreaker.core.structures.menu.MenuItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
                        Log.i("Item", document.getId() + " => " + document.getData());
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
