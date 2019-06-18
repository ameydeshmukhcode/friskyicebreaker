package com.frisky.icebreaker.pubs;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.Pub;
import com.frisky.icebreaker.ui.assistant.UIAssistant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PubListAdapter extends RecyclerView.Adapter<PubListAdapter.PubViewHolder> {

    private final Context mContext;
    private List<Pub> mPubList;

    static class PubViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView mPubCard;
        ImageView mImage;
        TextView mTitle;
        TextView mTags;
        TextView mRating;
        TextView mLocation;
        PubViewHolder(View v) {
            super(v);
            mImage = v.findViewById(R.id.image_pub);
            mTitle = v.findViewById(R.id.text_title);
            mTags = v.findViewById(R.id.text_tag_list);
            mRating = v.findViewById(R.id.text_rating);
            mLocation = v.findViewById(R.id.text_location);
            mPubCard = v.findViewById(R.id.card_pub);
        }
    }

    PubListAdapter(List<Pub> pubList, Context context) {
        this.mPubList = pubList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public PubListAdapter.PubViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_pub, viewGroup, false);

        return new PubViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull final PubViewHolder viewHolder, int i) {
        String tagList = "";
        final Pub pub = mPubList.get(i);

        Picasso.get().load(pub.getImageUri()).into(viewHolder.mImage);

        viewHolder.mTitle.setText(pub.getName());

        for (String tag: pub.getTags()) {
            tagList = tagList.concat(tag + " | ");
        }

        viewHolder.mLocation.setText(pub.getLocation());

        double pubRating = pub.getRating();

        viewHolder.mTags.setText(tagList.substring(0, tagList.length() - 3));
        viewHolder.mRating.setText(String.valueOf(pubRating));

        viewHolder.mRating.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().
                getColor(UIAssistant.getInstance().getRatingBadgeColor(pubRating))));

        viewHolder.mPubCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pubView = new Intent(mContext, PubActivity.class);
                pubView.putExtra("id", pub.getID());
                pubView.putExtra("name", viewHolder.mTitle.getText());
                pubView.putExtra("tags", viewHolder.mTags.getText());
                pubView.putExtra("location", viewHolder.mLocation.getText());
                pubView.putExtra("rating", viewHolder.mRating.getText());
                mContext.startActivity(pubView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPubList.size();
    }
}
