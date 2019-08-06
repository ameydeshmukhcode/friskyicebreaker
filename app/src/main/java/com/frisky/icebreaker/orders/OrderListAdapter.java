package com.frisky.icebreaker.orders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;
import com.frisky.icebreaker.ui.assistant.UIAssistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<OrderItem> mOrderList;

    OrderListAdapter(Context context, ArrayList<OrderItem> orderList) {
        this.mContext = context;
        this.mOrderList = orderList;
    }

    static class OrderListViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mStatus;
        TextView mItemCount;
        TextView mItemTotal;
        ImageView mStatusImage;
        OrderListViewHolder(@NonNull View view) {
            super(view);
            mName = view.findViewById(R.id.text_name);
            mStatus = view.findViewById(R.id.text_item_status);
            mItemCount = view.findViewById(R.id.text_item_count);
            mItemTotal = view.findViewById(R.id.text_cart_item_total);
            mStatusImage = view.findViewById(R.id.image_item_status);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_order_item, viewGroup, false);

        return new OrderListAdapter.OrderListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OrderListAdapter.OrderListViewHolder orderItemHolder = (OrderListAdapter.OrderListViewHolder) viewHolder;
        OrderItem orderItem = Objects.requireNonNull(mOrderList.get(position));
        OrderStatus status = orderItem.getStatus();
        orderItemHolder.mName.setText(orderItem.getName());
        orderItemHolder.mItemCount.setText(String.valueOf(orderItem.getCount()));
        orderItemHolder.mItemTotal.setText(String.valueOf(orderItem.getTotal()));
        orderItemHolder.mStatus.setText(UIAssistant.getInstance().getStatusText(status));
        orderItemHolder.mStatus.setTextColor(ColorStateList.valueOf(mContext.getApplicationContext()
                .getColor(UIAssistant.getInstance().getStatusColor(status))));
        orderItemHolder.mStatusImage.setImageResource(UIAssistant.getInstance().getStatusIcon(status));
        orderItemHolder.mStatusImage.setImageTintList(ColorStateList.valueOf(mContext.getApplicationContext()
                .getColor(UIAssistant.getInstance().getStatusColor(status))));
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }
}
