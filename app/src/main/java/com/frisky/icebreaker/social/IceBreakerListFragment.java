package com.frisky.icebreaker.social;

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
import com.frisky.icebreaker.core.structures.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class IceBreakerListFragment extends Fragment {

    protected List<User> usersList = new ArrayList<>();
    RecyclerView.Adapter iceBreakerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView mRecyclerUsersView;
        RecyclerView.LayoutManager mUsersViewLayoutManager;

        View view = inflater.inflate(R.layout.fragment_recycler_view, null);;

        mRecyclerUsersView = view.findViewById(R.id.recycler_view);

        // use a linear layout manager
        mUsersViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerUsersView.setLayoutManager(mUsersViewLayoutManager);

        prepareUserData();

        iceBreakerAdapter = new IceBreakerListViewAdapter(usersList, getContext());
        mRecyclerUsersView.setAdapter(iceBreakerAdapter);

        return view;
    }

    private void prepareUserData() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("User", document.getId() + " => " + document.getData());
                                if (user == null)
                                    return;
                                if (document.getId().equals(user.getUid()))
                                    return;
                                String name = document.getString("name");
                                String bio = document.getString("bio");
                                User user = new User(document.getId(), name, bio, 0, null);
                                usersList.add(user);
                                iceBreakerAdapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
