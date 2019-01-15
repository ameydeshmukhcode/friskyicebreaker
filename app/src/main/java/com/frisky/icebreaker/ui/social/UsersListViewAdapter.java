package com.frisky.icebreaker.ui.social;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.UserInfoMode;
import com.frisky.icebreaker.ui.profile.ViewProfileActivity;

import java.util.List;

public class UsersListViewAdapter extends RecyclerView.Adapter<UsersListViewAdapter.UsersListViewHolder> {

    private final Context mContext;
    private List<String> mUsersList;
    private UserInfoMode mUserInfoMode;

    public static class UsersListViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView mCard;
        public TextView mName;
        public UsersListViewHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.text_name);
            mCard = v.findViewById(R.id.card_user);
        }
    }

    public UsersListViewAdapter(List<String> usersList, UserInfoMode userInfoMode, Context context) {
        this.mUsersList = usersList;
        this.mUserInfoMode = userInfoMode;
        this.mContext = context;
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
    public void onBindViewHolder(@NonNull final UsersListViewHolder pingsViewHolder, int i) {
        String ping = mUsersList.get(i);
        pingsViewHolder.mName.setText(ping);

        pingsViewHolder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewUser = new Intent(mContext, ViewProfileActivity.class);
                viewUser.putExtra("name", pingsViewHolder.mName.getText());
                mContext.startActivity(viewUser);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }
}
