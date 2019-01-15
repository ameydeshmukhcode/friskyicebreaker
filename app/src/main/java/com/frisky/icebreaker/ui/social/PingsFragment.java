package com.frisky.icebreaker.ui.social;

import com.frisky.icebreaker.core.base.UserInfoFragment;
import com.frisky.icebreaker.core.structures.UserInfoMode;

public class PingsFragment extends UserInfoFragment {
    public PingsFragment() {
        setUserInfoMode(UserInfoMode.PING);
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

        user = "Creme";
        usersList.add(user);

        user = "Brute";
        usersList.add(user);

        user = "Somebody";
        usersList.add(user);

        mUsersViewAdapter.notifyDataSetChanged();
    }
}
