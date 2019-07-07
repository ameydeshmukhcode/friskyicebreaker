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
        TextView mCuisine;
        TextView mRating;
        TextView mLocation;
        PubViewHolder(View v) {
            super(v);
            mImage = v.findViewById(R.id.image_pub);
            mTitle = v.findViewById(R.id.text_title);
            mCuisine = v.findViewById(R.id.text_cuisine);
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
        final Restaurant restaurant = mRestaurantList.get(i);

        Picasso.get().load(restaurant.getImageUri()).into(viewHolder.mImage);

        viewHolder.mTitle.setText(restaurant.getName());
        viewHolder.mLocation.setText(restaurant.getLocation());

        double pubRating = restaurant.getRating();

        viewHolder.mCuisine.setText(restaurant.getCuisine());
        viewHolder.mRating.setText(String.valueOf(pubRating));

        viewHolder.mRating.setBackgroundTintList(ColorStateList.valueOf(mContext
                .getColor(UIAssistant.getInstance().getRatingBadgeColor(pubRating))));

        viewHolder.mPubCard.setOnClickListener(v -> {
            Intent pubView = new Intent(mContext, RestaurantActivity.class);
            pubView.putExtra("id", restaurant.getID());
            pubView.putExtra("name", viewHolder.mTitle.getText());
            pubView.putExtra("tags", viewHolder.mCuisine.getText());
            pubView.putExtra("location", viewHolder.mLocation.getText());
            pubView.putExtra("rating", viewHolder.mRating.getText());
            mContext.startActivity(pubView);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
}
