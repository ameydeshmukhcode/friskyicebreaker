package com.frisky.icebreaker.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.UserInfoMode;
import com.frisky.icebreaker.social.UsersListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class UserInfoFragment extends Fragment {

    protected List<String> usersList = new ArrayList<>();

    public void setUserInfoMode(UserInfoMode mUserInfoMode) {
        this.mUserInfoMode = mUserInfoMode;
    }

    private UserInfoMode mUserInfoMode = null;

    private RecyclerView mRecyclerUsersView;
    protected RecyclerView.Adapter mUsersViewAdapter;
    private RecyclerView.LayoutManager mUsersViewLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        view = setViewLayout(view, inflater);
        mRecyclerUsersView = view.findViewById(R.id.recycler_view);

        // use a linear layout manager
        mUsersViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerUsersView.setLayoutManager(mUsersViewLayoutManager);

        // specify an adapter (see also next example)
        mUsersViewAdapter = new UsersListViewAdapter(usersList, mUserInfoMode, getContext());
        mRecyclerUsersView.setAdapter(mUsersViewAdapter);

        prepareUserData();

        return view;
    }

    public abstract View setViewLayout(View view, LayoutInflater inflater);

    //Reimplement this in derived class to update user data in a specific Fragment
    public abstract void prepareUserData();
}