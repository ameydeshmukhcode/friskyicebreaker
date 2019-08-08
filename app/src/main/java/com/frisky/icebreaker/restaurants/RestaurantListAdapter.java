package com.frisky.icebreaker.restaurants;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.Restaurant;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.frisky.icebreaker.ui.assistant.UIAssistant.getRatingBadgeColor;

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
        PubViewHolder(View view) {
            super(view);
            mImage = view.findViewById(R.id.image_pub);
            mTitle = view.findViewById(R.id.text_title);
            mCuisine = view.findViewById(R.id.text_cuisine);
            mRating = view.findViewById(R.id.text_rating);
            mLocation = view.findViewById(R.id.text_location);
            mPubCard = view.findViewById(R.id.card_pub);
        }
    }

    RestaurantListAdapter(List<Restaurant> restaurantList, Context context) {
        this.mRestaurantList = restaurantList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RestaurantListAdapter.PubViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int viewType) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_restaurant, viewGroup, false);

        return new PubViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull final PubViewHolder viewHolder, int position) {
        final Restaurant restaurant = mRestaurantList.get(position);

        Picasso.get().load(restaurant.getImageUri()).into(viewHolder.mImage);

        viewHolder.mTitle.setText(restaurant.getName());
        viewHolder.mLocation.setText(restaurant.getLocation());

        double pubRating = restaurant.getRating();

        viewHolder.mCuisine.setText(restaurant.getCuisine());
        viewHolder.mRating.setText(String.valueOf(pubRating));

        viewHolder.mRating.setBackgroundTintList(ColorStateList.valueOf(mContext
                .getColor(getRatingBadgeColor(pubRating))));

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
