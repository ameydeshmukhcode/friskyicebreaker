package com.frisky.icebreaker.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;

public class EditImagesFragment extends Fragment {

    RecyclerView mRecyclerImageGridView;
    RecyclerView.LayoutManager mEditImagesGridLayoutManager;
    RecyclerView.Adapter mImageGridAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_recycler_view, null);

        mRecyclerImageGridView = view.findViewById(R.id.recycler_view);

        mRecyclerImageGridView.setHasFixedSize(true);

        // use a grid layout manager
        mEditImagesGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerImageGridView.setLayoutManager(mEditImagesGridLayoutManager);

        // specify an adapter (see also next example)
        mImageGridAdapter = new EditImagesAdapter(getContext());
        mRecyclerImageGridView.setAdapter(mImageGridAdapter);

        return view;
    }
}
