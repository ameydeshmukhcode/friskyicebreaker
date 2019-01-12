package com.frisky.icebreaker.core.base;

import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.UserInfoMode;
import com.frisky.icebreaker.ui.social.UsersListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class UserInfoFragment extends Fragment {

    private List<String> usersList = new ArrayList<>();

    public void setUserInfoMode(UserInfoMode mUserInfoMode) {
        this.mUserInfoMode = mUserInfoMode;
    }

    private UserInfoMode mUserInfoMode = null;

    private RecyclerView mRecyclerUsersView;
    private RecyclerView.Adapter mUsersViewAdapter;
    private RecyclerView.LayoutManager mUsersViewLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_list_view, null);
        mRecyclerUsersView = view.findViewById(R.id.recycler_users_list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerUsersView.setHasFixedSize(true);

        // use a linear layout manager
        mUsersViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerUsersView.setLayoutManager(mUsersViewLayoutManager);

        // specify an adapter (see also next example)
        mUsersViewAdapter = new UsersListViewAdapter(usersList, mUserInfoMode);
        mRecyclerUsersView.setAdapter(mUsersViewAdapter);
        mDividerItemDecoration = new DividerItemDecoration(mRecyclerUsersView.getContext(),
                mUsersViewLayoutManager.getLayoutDirection());
        mRecyclerUsersView.addItemDecoration(mDividerItemDecoration);

        prepareUserData();

        return view;
    }

    private void prepareUserData() {
        String user = new String("Dj");
        usersList.add(user);

        user = new String("BJ");
        usersList.add(user);

        user = new String("CJ");
        usersList.add(user);

        user = new String("Amey");
        usersList.add(user);

        user = new String("Mah Dude");
        usersList.add(user);

        user = new String("BJ");
        usersList.add(user);

        user = new String("CJ");
        usersList.add(user);

        user = new String("Amey");
        usersList.add(user);

        user = new String("Mah Dude");
        usersList.add(user);

        mUsersViewAdapter.notifyDataSetChanged();
    }
}