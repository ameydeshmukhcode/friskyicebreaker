package com.frisky.icebreaker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SocialPagerAdapter extends FragmentStatePagerAdapter {

    public SocialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override

    public Fragment getItem(int i) {
        switch (i) {
            case 0: return new PubViewFragment();
            case 1: return new ProfileFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Pings";
            case 1: return "Friends";
            default: return null;
        }
    }
}
