package com.frisky.icebreaker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class PubViewFragment extends Fragment {

    private ImageButton mFiltersButton;

    private RecyclerView mRecyclerPubView;
    private RecyclerView.Adapter mPubViewAdapter;
    private RecyclerView.LayoutManager mPubViewLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_pub_view, null);

        mFiltersButton = view.findViewById(R.id.button_filters);
        mFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogFragment();
            }
        });

        return view;
    }

    public void showBottomSheetDialogFragment() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
    }
}
