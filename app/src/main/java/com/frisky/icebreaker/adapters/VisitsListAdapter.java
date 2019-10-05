package com.frisky.icebreaker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.activities.VisitActivity;
import com.frisky.icebreaker.core.structures.Visit;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VisitsListAdapter extends RecyclerView.Adapter<VisitsListAdapter.OrderSummaryHolder> {

    private final Context mContext;
    private List<Visit> mVisitList;

    static class OrderSummaryHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mTime;
        TextView mAmount;
        ImageView mRestaurantImage;
        MaterialCardView mOrderCard;
        OrderSummaryHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.text_restaurant_name);
            mTime = view.findViewById(R.id.text_time);
            mOrderCard = view.findViewById(R.id.card_order_history);
            mRestaurantImage = view.findViewById(R.id.image_restaurant);
            mAmount = view.findViewById(R.id.text_total);
        }
    }

    public VisitsListAdapter(List<Visit> visitList, Context context) {
        this.mVisitList = visitList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public OrderSummaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_visit, parent, false);

        return new VisitsListAdapter.OrderSummaryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderSummaryHolder holder, int position) {
        Visit summary = mVisitList.get(position);

        holder.mName.setText(summary.getRestaurantName());
        holder.mTime.setText(summary.getEndTime());
        @SuppressLint("DefaultLocale")
        String amount = String.format("%.2f", summary.getTotalAmount());
        holder.mAmount.setText(amount);

        Picasso.get().load(summary.getRestaurantImage()).into(holder.mRestaurantImage);

        holder.mOrderCard.setOnClickListener(v -> {
            Intent showSummary = new Intent(mContext, VisitActivity.class);
            showSummary.putExtra("session_id", summary.getSessionID());
            showSummary.putExtra("restaurant_id", summary.getRestaurantID());
            showSummary.putExtra("restaurant_name", summary.getRestaurantName());
            mContext.startActivity(showSummary);
        });
    }

    @Override
    public int getItemCount() {
        return mVisitList.size();
    }
}
