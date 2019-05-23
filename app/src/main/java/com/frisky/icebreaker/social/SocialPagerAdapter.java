package com.frisky.icebreaker.social;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SocialPagerAdapter extends FragmentStatePagerAdapter {

    private static final int SOCIAL_TAB_COUNT = 2;

    public SocialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0: return new ChatsFragment();
            case 1: return new PendingFragment();
        }
        return null;
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
