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
import android.widget.Button;

import com.frisky.icebreaker.R;

public class SocialFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_social_view, null);

        final ViewPager viewPager = view.findViewById(R.id.pager_social);
        SocialPagerAdapter myPagerAdapter = new SocialPagerAdapter(getFragmentManager());
        viewPager.setAdapter(myPagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_social);
        tabLayout.setupWithViewPager(viewPager, true);

        return view;
    }
}
