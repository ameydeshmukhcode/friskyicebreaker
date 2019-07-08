package com.frisky.icebreaker.social;

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
import com.frisky.icebreaker.core.structures.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.frisky.icebreaker.orders.OrderingAssistant.SESSION_ACTIVE;

public class IceBreakerFragment extends Fragment {

    private List<User> mUsersList = new ArrayList<>();
    private RecyclerView.Adapter mIceBreakerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_icebreaker, viewGroup, false);

        RecyclerView mRecyclerUsersView;
        RecyclerView.LayoutManager mUsersViewLayoutManager;

        mRecyclerUsersView = view.findViewById(R.id.recycler_view);

        if (SESSION_ACTIVE) {
            mRecyclerUsersView.setPadding(0, 0, 0, 0);
            mRecyclerUsersView.setPadding(0, 0, 0, 225);
            mRecyclerUsersView.setClipToPadding(false);
        }

        // use a linear layout manager
        mUsersViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerUsersView.setLayoutManager(mUsersViewLayoutManager);

        prepareUserData();

        mIceBreakerAdapter = new IceBreakerListViewAdapter(mUsersList, getContext());
        mRecyclerUsersView.setAdapter(mIceBreakerAdapter);

        return view;
    }

    private void prepareUserData() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mFirestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("User", document.getId() + " => " + document.getData());
                                String name = document.getString("name");
                                String bio = document.getString("bio");
                                User user = new User(document.getId(), name, bio, 0, null);
                                if (firebaseUser != null && !document.getId().equals(firebaseUser.getUid())) {
                                    mUsersList.add(user);
                                    mIceBreakerAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                    else {
                        Log.e("error", "Error getting documents: ", task.getException());
                    }
                });
    }
}
