package com.frisky.icebreaker.orders;

import android.content.Context;
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

    private Context mContext;
    private HashMap<MenuItem, MutableInt> mOrderList;

    OrderListAdapter(Context context, HashMap<MenuItem, MutableInt> orderList) {
        this.mContext = context;
        this.mOrderList = orderList;
    }

    static class OrderListViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mPrice;
        TextView mStatus;
        TextView mCount;
        TextView mItemTotal;
        OrderListViewHolder(@NonNull View view) {
            super(view);
            mName = view.findViewById(R.id.text_name);
            mPrice = view.findViewById(R.id.text_price);
            mStatus = view.findViewById(R.id.in_cart);
            mCount = view.findViewById(R.id.text_item_count);
            mItemTotal = view.findViewById(R.id.text_cart_item_total);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_cart_item, viewGroup, false);

        return new OrderListAdapter.OrderListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OrderListViewHolder orderHolder = (OrderListViewHolder) viewHolder;
        MenuItem item = (MenuItem) Objects.requireNonNull(mOrderList.keySet().toArray())[position];
        MutableInt count = (MutableInt) mOrderList.values().toArray()[position];
        orderHolder.mName.setText(item.getName());
        orderHolder.mCount.setText(String.valueOf(count.getValue()));
        orderHolder.mPrice.setText(String.valueOf(item.getPrice()));
        orderHolder.mItemTotal.setText(String.valueOf(item.getPrice() * count.getValue()));
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }
}
