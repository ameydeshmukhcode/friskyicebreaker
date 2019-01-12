package com.frisky.icebreaker.ui.social;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.UserInfoMode;

import java.util.List;

public class UsersListViewAdapter extends RecyclerView.Adapter<UsersListViewAdapter.UsersListViewHolder> {

    private List<String> mUsersList;
    private UserInfoMode mUserInfoMode;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class UsersListViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView mName;
        public UsersListViewHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.text_name);
        }
    }

    public UsersListViewAdapter(List<String> usersList, UserInfoMode userInfoMode) {
        this.mUsersList = usersList;
        this.mUserInfoMode = userInfoMode;
    }

    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = null;

        switch (mUserInfoMode) {
            case PREVIEW: itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_user_preview, viewGroup, false); break;
            case PING: itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_user_ping, viewGroup, false); break;
            case FRIEND: itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_user_friend, viewGroup, false); break;
        }

        return new UsersListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListViewHolder pingsViewHolder, int i) {
        String ping = mUsersList.get(i);
        pingsViewHolder.mName.setText(ping);
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }
}
