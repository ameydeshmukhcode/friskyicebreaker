package com.frisky.icebreaker.pubs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.components.dialogs.FiltersDialog;

public class PubViewFragment extends Fragment {

    ImageButton mFiltersButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_pubs, null);

        mFiltersButton = view.findViewById(R.id.button_filters);
        mFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFiltersDialog(getContext());
            }
        });

        Fragment pubListFragment = new PubListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_pubs, pubListFragment).commit();

        return view;
    }

    private void launchFiltersDialog (Context context) {
        FiltersDialog filtersDialog = new FiltersDialog(context);
        filtersDialog.show();
    }
}
