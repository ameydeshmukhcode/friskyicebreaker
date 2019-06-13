package com.frisky.icebreaker.social;

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

public class ChatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView mRecyclerUsersView;
        RecyclerView.LayoutManager mUsersViewLayoutManager;

        View view = inflater.inflate(R.layout.fragment_recycler_view, null);

        mRecyclerUsersView = view.findViewById(R.id.recycler_view);

        // use a linear layout manager
        mUsersViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerUsersView.setLayoutManager(mUsersViewLayoutManager);

        return view;
    }
}
