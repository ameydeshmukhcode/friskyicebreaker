package com.frisky.icebreaker.social;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;

public class SocialFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_social, null);

        final ViewPager viewPager = view.findViewById(R.id.pager_social);
        SocialPagerAdapter myPagerAdapter = new SocialPagerAdapter(getFragmentManager());
        viewPager.setAdapter(myPagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_social);
        tabLayout.setupWithViewPager(viewPager, true);

        return view;
    }
}
