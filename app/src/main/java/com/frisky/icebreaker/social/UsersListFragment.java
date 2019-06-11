package com.frisky.icebreaker.social;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.Pub;
import com.frisky.icebreaker.core.structures.User;
import com.frisky.icebreaker.ui.base.UserInfoFragment;
import com.frisky.icebreaker.core.structures.UserInfoMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class UsersListFragment extends UserInfoFragment {
    public UsersListFragment() {
        setUserInfoMode(UserInfoMode.ICEBREAKER);
    }

    @Override
    public View setViewLayout(View view, LayoutInflater inflater) {
        view =  inflater.inflate(R.layout.fragment_recycler_view, null);
        return view;
    }

    @Override
    public void prepareUserData() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Whatever", document.getId() + " => " + document.getData());
                                String name = document.getString("name");
                                String bio = document.getString("bio");
                                User user = new User(document.getId(), name, bio, 0, null);
                                usersList.add(user);
                                mUsersViewAdapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
