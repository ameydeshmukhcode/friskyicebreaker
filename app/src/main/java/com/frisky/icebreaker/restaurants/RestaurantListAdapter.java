package com.frisky.icebreaker.restaurants;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import androidx.annotation.NonNull;
import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.Restaurant;
import com.frisky.icebreaker.ui.assistant.UIAssistant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.PubViewHolder> {

    private final Context mContext;
    private List<Restaurant> mRestaurantList;

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

    RestaurantListAdapter(List<Restaurant> restaurantList, Context context) {
        this.mRestaurantList = restaurantList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RestaurantListAdapter.PubViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_restaurant, viewGroup, false);

        return new PubViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull final PubViewHolder viewHolder, int i) {
        String tagList = "";
        final Restaurant restaurant = mRestaurantList.get(i);

        Picasso.get().load(restaurant.getImageUri()).into(viewHolder.mImage);

        viewHolder.mTitle.setText(restaurant.getName());

        for (String tag: restaurant.getTags()) {
            tagList = tagList.concat(tag + " | ");
        }

        viewHolder.mLocation.setText(restaurant.getLocation());

        double pubRating = restaurant.getRating();

        viewHolder.mTags.setText(tagList.substring(0, tagList.length() - 3));
        viewHolder.mRating.setText(String.valueOf(pubRating));

        viewHolder.mRating.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().
                getColor(UIAssistant.getInstance().getRatingBadgeColor(pubRating))));

        viewHolder.mPubCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pubView = new Intent(mContext, RestaurantActivity.class);
                pubView.putExtra("id", restaurant.getID());
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
        return mRestaurantList.size();
    }
}
