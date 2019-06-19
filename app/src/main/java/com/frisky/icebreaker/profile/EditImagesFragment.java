package com.frisky.icebreaker.profile;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        mRecyclerImageGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // use a grid layout manager
        mEditImagesGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerImageGridView.setLayoutManager(mEditImagesGridLayoutManager);

        // specify an adapter (see also next example)
        mImageGridAdapter = new EditImagesAdapter(getContext(), getActivity());
        mRecyclerImageGridView.setAdapter(mImageGridAdapter);

        return view;
    }
}
