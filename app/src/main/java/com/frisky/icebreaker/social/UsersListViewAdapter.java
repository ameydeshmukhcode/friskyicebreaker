package com.frisky.icebreaker.social;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.User;
import com.frisky.icebreaker.core.structures.UserInfoMode;
import com.frisky.icebreaker.ui.assistant.UIAssistant;
import com.frisky.icebreaker.profile.ViewUserActivity;

import java.util.List;

public class UsersListViewAdapter extends RecyclerView.Adapter<UsersListViewAdapter.UsersListViewHolder> {

    private final Context mContext;
    private List<User> mUsersList;
    private UserInfoMode mUserInfoMode;

    static class UsersListViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView mCard;
        TextView mName;
        ImageView mPicture;
        UsersListViewHolder(View v) {
            super(v);
            mPicture = v.findViewById(R.id.image_user);
            mName = v.findViewById(R.id.text_name);
            mCard = v.findViewById(R.id.card_user);
        }
    }

    public UsersListViewAdapter(List<User> usersList, UserInfoMode userInfoMode, Context context) {
        this.mUsersList = usersList;
        this.mUserInfoMode = userInfoMode;
        this.mContext = context;
    }

    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = null;

        switch (mUserInfoMode) {
            case ICEBREAKER:
                itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_user_preview, viewGroup, false);
                break;
            case PENDING:
                itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_user_pending, viewGroup, false);
                break;
            case CHAT:
                itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_user_chat, viewGroup, false);
                break;
        }

        return new UsersListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersListViewHolder viewHolder, int i) {
        User user = mUsersList.get(i);
        viewHolder.mName.setText(user.getName());

        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.placeholder);
        viewHolder.mPicture.setImageBitmap(UIAssistant.getInstance().getCircleBitmap(bm));

        switch (mUserInfoMode) {
            case ICEBREAKER:
                viewHolder.mCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewUser = new Intent(mContext, ViewUserActivity.class);
                        viewUser.putExtra("name", viewHolder.mName.getText());
                        mContext.startActivity(viewUser);
                    }
                });
                break;
            case PENDING:
                viewHolder.mCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewUser = new Intent(mContext, ChatActivity.class);
                        viewUser.putExtra("name", viewHolder.mName.getText());
                        mContext.startActivity(viewUser);
                    }
                });
                break;
            case CHAT:
                viewHolder.mCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewUser = new Intent(mContext, ChatActivity.class);
                        viewUser.putExtra("name", viewHolder.mName.getText());
                        mContext.startActivity(viewUser);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }
}
