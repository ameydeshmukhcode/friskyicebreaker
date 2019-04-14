package com.frisky.icebreaker.ui.social;

import com.frisky.icebreaker.core.base.UserInfoFragment;
import com.frisky.icebreaker.core.structures.UserInfoMode;

public class ChatsFragment extends UserInfoFragment {
    public ChatsFragment() {
        setUserInfoMode(UserInfoMode.FRIEND);
    }

    @Override
    public void prepareUserData() {
        String user = "Dj";
        this.usersList.add(user);

        user = "BJ";
        usersList.add(user);

        user = "CJ";
        usersList.add(user);

        user = "Amey";
        usersList.add(user);

        user = "Mah Dude";
        usersList.add(user);

        user = "BJ";
        usersList.add(user);

        user = "CJ";
        usersList.add(user);

        user = "Amey";
        usersList.add(user);

        user = "Mah Dude";
        usersList.add(user);

        mUsersViewAdapter.notifyDataSetChanged();
    }
}
