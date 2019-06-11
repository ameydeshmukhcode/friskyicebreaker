package com.frisky.icebreaker.social;

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

    }
}
