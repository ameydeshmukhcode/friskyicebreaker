package com.frisky.icebreaker.ui.social;

import com.frisky.icebreaker.core.base.UserInfoFragment;
import com.frisky.icebreaker.core.structures.UserInfoMode;

public class UsersListFragment extends UserInfoFragment {
    public UsersListFragment() {
        setUserInfoMode(UserInfoMode.PREVIEW);
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
