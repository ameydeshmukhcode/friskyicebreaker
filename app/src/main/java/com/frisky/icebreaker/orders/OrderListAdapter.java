package com.frisky.icebreaker.orders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.core.structures.MutableInt;

import java.util.HashMap;
import java.util.Objects;

public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private HashMap<MenuItem, MutableInt> orderList;

    OrderListAdapter(Context context, HashMap<MenuItem, MutableInt> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    static class OrderListViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mPrice;
        TextView mStatus;
        TextView mCount;

        OrderListViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.text_name);
            mPrice = itemView.findViewById(R.id.text_price);
            mStatus = itemView.findViewById(R.id.text_status);
            mCount = itemView.findViewById(R.id.text_count);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_order_item, parent, false);

        return new OrderListAdapter.OrderListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OrderListViewHolder orderHolder = (OrderListViewHolder) holder;
        MenuItem item = (MenuItem) Objects.requireNonNull(orderList.keySet().toArray())[position];
        MutableInt count = (MutableInt) orderList.values().toArray()[position];
        orderHolder.mName.setText(item.getName());
        orderHolder.mCount.setText(String.valueOf(count.getValue()));
        orderHolder.mPrice.setText(String.valueOf(item.getPrice() * count.getValue()));
        orderHolder.mStatus.setTextColor(ColorStateList.valueOf(context.getColor(R.color.rating_low)));
        orderHolder.mStatus.setText(R.string.status_pending);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
