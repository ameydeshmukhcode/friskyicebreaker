package com.frisky.icebreaker.orders;

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
import com.frisky.icebreaker.core.structures.menu.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuItemListFragment extends Fragment {

    private List<MenuItem> menu = new ArrayList<>();

    private RecyclerView.Adapter mMenuListViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_recycler_view, null);

        RecyclerView mRecyclerMenuListView;
        mRecyclerMenuListView = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mMenuListViewLayoutManager;
        // use a linear layout manager
        mMenuListViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerMenuListView.setLayoutManager(mMenuListViewLayoutManager);

        // specify an adapter (see also next example)
        mMenuListViewAdapter = new MenuItemListAdapter(menu);
        mRecyclerMenuListView.setAdapter(mMenuListViewAdapter);

        prepareMenuData();

        return view;
    }

    private void prepareMenuData() {
        MenuItem item = new MenuItem("First", "Something", 100);
        menu.add(item);

        item = new MenuItem("Second", "Something", 150);
        menu.add(item);

        item = new MenuItem("Third", "Something", 80);
        menu.add(item);

        item = new MenuItem("Fourth", "Something", 130);
        menu.add(item);

        item = new MenuItem("Fifth", "Something", 220);
        menu.add(item);

        item = new MenuItem("Sixth", "Something", 322);
        menu.add(item);

        item = new MenuItem("Seventh", "Something", 30);
        menu.add(item);

        mMenuListViewAdapter.notifyDataSetChanged();
    }
}
