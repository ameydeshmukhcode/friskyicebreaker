package com.frisky.icebreaker.ui.social;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;

public class SocialFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_social_view, null);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager_social);
        SocialPagerAdapter myPagerAdapter = new SocialPagerAdapter(getFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs_social);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setElevation(5);

        return view;
    }
}
