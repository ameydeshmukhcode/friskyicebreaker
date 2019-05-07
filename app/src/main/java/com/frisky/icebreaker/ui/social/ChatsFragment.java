package com.frisky.icebreaker.ui.social;

import android.view.LayoutInflater;
import android.view.View;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.base.UserInfoFragment;
import com.frisky.icebreaker.core.structures.UserInfoMode;

public class ChatsFragment extends UserInfoFragment {
    public ChatsFragment() {
        setUserInfoMode(UserInfoMode.CHAT);
    }

    @Override
    public View setViewLayout(View view, LayoutInflater inflater) {
        view =  inflater.inflate(R.layout.fragment_recycler_view, null);
        return view;       }

    @Override
    public void prepareUserData() {
        String user = "DJ";
        this.usersList.add(user);

        user = "BJ";
        usersList.add(user);

        user = "CJ";
        usersList.add(user);

        user = "Amey";
        usersList.add(user);

        user = "Mah Dude";
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
