package com.frisky.icebreaker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.frisky.icebreaker.fragments.ChatsFragment;
import com.frisky.icebreaker.fragments.PendingFragment;

public class SocialPagerAdapter extends FragmentStatePagerAdapter {

    private static final int SOCIAL_TAB_COUNT = 2;

    public SocialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new ChatsFragment();
            case 1: return new PendingFragment();
        }
        return new ChatsFragment();
    }

    @Override
    public int getCount() {
        return SOCIAL_TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Chats";
            case 1: return "Pending";
            default: return null;
        }
    }
}
