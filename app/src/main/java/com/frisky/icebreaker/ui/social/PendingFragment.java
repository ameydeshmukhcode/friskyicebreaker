package com.frisky.icebreaker.ui.social;

import android.view.LayoutInflater;
import android.view.View;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UserInfoFragment;
import com.frisky.icebreaker.core.structures.UserInfoMode;

public class PendingFragment extends UserInfoFragment {
    public PendingFragment() {
        setUserInfoMode(UserInfoMode.PENDING);
    }

    @Override
    public View setViewLayout(View view, LayoutInflater inflater) {
        view =  inflater.inflate(R.layout.fragment_recycler_view, null);
        return view;
    }

    @Override
    public void prepareUserData() {
        String user = "User";
        this.usersList.add(user);

        user = "Creme";
        usersList.add(user);

        user = "Brute";
        usersList.add(user);

        user = "Somebody";
        usersList.add(user);

        user = "Jon";
        usersList.add(user);

        user = "Need";
        usersList.add(user);

        user = "More";
        usersList.add(user);

        user = "Samples";
        usersList.add(user);

        mUsersViewAdapter.notifyDataSetChanged();
    }
}
