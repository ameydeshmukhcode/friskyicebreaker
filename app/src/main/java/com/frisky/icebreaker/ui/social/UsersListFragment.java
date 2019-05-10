package com.frisky.icebreaker.ui.social;

import android.view.LayoutInflater;
import android.view.View;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UserInfoFragment;
import com.frisky.icebreaker.core.structures.UserInfoMode;

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
        String user = "View";
        this.usersList.add(user);

        user = "Users";
        usersList.add(user);

        user = "Up";
        usersList.add(user);

        user = "In";
        usersList.add(user);

        user = "This";
        usersList.add(user);

        user = "Place";
        usersList.add(user);

        mUsersViewAdapter.notifyDataSetChanged();
    }
}
