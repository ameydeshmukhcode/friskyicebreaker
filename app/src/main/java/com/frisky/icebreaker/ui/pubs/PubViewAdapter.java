package com.frisky.icebreaker.ui.pubs;

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
import com.frisky.icebreaker.core.structures.Pub;
import com.frisky.icebreaker.ui.assistant.UIAssistant;

import java.util.List;

public class PubViewAdapter extends RecyclerView.Adapter<PubViewAdapter.PubViewHolder> {

    private final Context mContext;
    private List<Pub> mPubList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class PubViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView mPubCard;
        TextView mTitle;
        TextView mTags;
        TextView mRating;
        TextView mLocation;
        PubViewHolder(View v) {
            super(v);
            mTitle = v.findViewById(R.id.title);
            mTags = v.findViewById(R.id.tag_list);
            mRating = v.findViewById(R.id.rating);
            mLocation = v.findViewById(R.id.location);
            mPubCard = v.findViewById(R.id.card_pub);
        }
    }

    PubViewAdapter(List<Pub> pubList, Context context) {
        this.mPubList = pubList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public PubViewAdapter.PubViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_pub, viewGroup, false);

        return new PubViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull final PubViewHolder viewHolder, int i) {
        String tagList = "";
        Pub pub = mPubList.get(i);
        viewHolder.mTitle.setText(pub.getName());

        for (String tag: pub.getTags()) {
            tagList += tag + " | ";
        }

        viewHolder.mLocation.setText(pub.getLocation());

        Double pubRating = pub.getRating();

        viewHolder.mTags.setText(tagList.substring(0, tagList.length() - 3));
        viewHolder.mRating.setText(Double.toString(pubRating));

        viewHolder.mRating.setBackgroundResource(UIAssistant.getInstance().getRatingBadgeBackground(pubRating));

        viewHolder.mPubCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pubView = new Intent(mContext, PubActivity.class);
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
