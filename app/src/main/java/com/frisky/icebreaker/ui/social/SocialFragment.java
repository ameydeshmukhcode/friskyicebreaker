package com.frisky.icebreaker.ui.social;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.frisky.icebreaker.R;

public class SocialFragment extends Fragment {

    Button mChatsButton;
    Button mPendingButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_social_view, null);

        final ViewPager viewPager = view.findViewById(R.id.pager_social);
        SocialPagerAdapter myPagerAdapter = new SocialPagerAdapter(getFragmentManager());
        viewPager.setAdapter(myPagerAdapter);

        mChatsButton = view.findViewById(R.id.button_chats);
        mPendingButton = view.findViewById(R.id.button_pending);

        mChatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

        mPendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });

        return view;
    }
}
