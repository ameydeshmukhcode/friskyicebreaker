package com.frisky.icebreaker.ui.social;

import com.frisky.icebreaker.core.base.UserInfoFragment;
import com.frisky.icebreaker.core.structures.UserInfoMode;

public class FriendsFragment extends UserInfoFragment {
    public FriendsFragment() {
        setUserInfoMode(UserInfoMode.FRIEND);
    }
}
