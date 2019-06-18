package com.frisky.icebreaker.orders;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;

public class MenuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_menu, null);

        String restID = getArguments().getString("restaurant_id");

        Bundle bundle = new Bundle();
        bundle.putString("restaurant_id", restID);

        Fragment menuItemListFragment = new MenuItemListFragment();
        menuItemListFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_submenu, menuItemListFragment).commit();

        return view;
    }
}
