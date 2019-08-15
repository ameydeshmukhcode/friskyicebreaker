package com.frisky.icebreaker.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.User;
import com.frisky.icebreaker.activities.ViewUserActivity;
import com.frisky.icebreaker.ui.assistant.CircularTransformation;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class IceBreakerListViewAdapter extends RecyclerView.Adapter<IceBreakerListViewAdapter.IceBreakerListViewHolder> {

    private final Context mContext;
    private List<User> mUsersList;

    static class IceBreakerListViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView mCard;
        TextView mName;
        ImageView mPicture;
        IceBreakerListViewHolder(View view) {
            super(view);
            mPicture = view.findViewById(R.id.image_user);
            mName = view.findViewById(R.id.text_name);
            mCard = view.findViewById(R.id.card_user);
        }
    }

    IceBreakerListViewAdapter(List<User> usersList, Context context) {
        this.mUsersList = usersList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public IceBreakerListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_user_preview, viewGroup, false);

        return new IceBreakerListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final IceBreakerListViewHolder viewHolder, int position) {
        final User user = mUsersList.get(position);
        viewHolder.mName.setText(user.getName());

        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference()
                .child("profile_images")
                .child(user.getID());

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).transform(new CircularTransformation()).into(viewHolder.mPicture);
            Log.d("Image Uri Downloaded", uri.toString());
        }).addOnFailureListener(e -> Log.e("Uri Download Failed", e.getMessage()));

        viewHolder.mCard.setOnClickListener(v -> {
            Intent viewUser = new Intent(mContext, ViewUserActivity.class);
            viewUser.putExtra("id", user.getID());
            viewUser.putExtra("name", user.getName());
            mContext.startActivity(viewUser);
        });
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }
}
