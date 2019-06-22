package com.frisky.icebreaker.social;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;

import com.frisky.icebreaker.ui.assistant.CircularTransformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.User;
import com.frisky.icebreaker.ui.assistant.UIAssistant;
import com.frisky.icebreaker.profile.ViewUserActivity;
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
        IceBreakerListViewHolder(View v) {
            super(v);
            mPicture = v.findViewById(R.id.image_user);
            mName = v.findViewById(R.id.text_name);
            mCard = v.findViewById(R.id.card_user);
        }
    }

    public IceBreakerListViewAdapter(List<User> usersList, Context context) {
        this.mUsersList = usersList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public IceBreakerListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = null;

        itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_user_preview, viewGroup, false);

        return new IceBreakerListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final IceBreakerListViewHolder viewHolder, int i) {
        final User user = mUsersList.get(i);
        viewHolder.mName.setText(user.getName());

        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference()
                .child("profile_images")
                .child(user.getID());

        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircularTransformation()).into(viewHolder.mPicture);
                Log.i("Image Uri Downloaded", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Uri Download Failed", e.getMessage());
            }
        });

        viewHolder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewUser = new Intent(mContext, ViewUserActivity.class);
                viewUser.putExtra("id", user.getID());
                viewUser.putExtra("name", user.getName());
                mContext.startActivity(viewUser);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }
}
