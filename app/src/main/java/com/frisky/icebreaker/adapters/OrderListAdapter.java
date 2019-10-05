package com.frisky.icebreaker.adapters;

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
import com.frisky.icebreaker.core.structures.OrderHeader;
import com.frisky.icebreaker.core.structures.OrderItem;
import com.frisky.icebreaker.core.structures.OrderStatus;

import java.util.ArrayList;

import static com.frisky.icebreaker.ui.assistant.UIAssistant.getStatusColor;
import static com.frisky.icebreaker.ui.assistant.UIAssistant.getStatusIcon;
import static com.frisky.icebreaker.ui.assistant.UIAssistant.getStatusText;

public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Object> mOrderList;

    private final int ORDER_HEADER = 77;
    private final int ORDER_ITEM = 88;

    public OrderListAdapter(Context context, ArrayList<Object> orderList) {
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

    static class OrderDetailsHeaderHolder extends RecyclerView.ViewHolder {
        TextView mRank;
        TextView mTime;
        OrderDetailsHeaderHolder(View view) {
            super(view);
            mRank = view.findViewById(R.id.text_order_rank);
            mTime = view.findViewById(R.id.text_order_time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mOrderList.get(position) instanceof OrderHeader) {
            return ORDER_HEADER;
        } else if (mOrderList.get(position) instanceof OrderItem) {
            return ORDER_ITEM;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;
        switch (viewType) {
            case ORDER_HEADER:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.card_order_header, viewGroup, false);

                return new OrderDetailsHeaderHolder(itemView);

            case ORDER_ITEM:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.card_order_item, viewGroup, false);

                return new OrderListViewHolder(itemView);
        }

        itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_order_item, viewGroup, false);

        return new OrderListAdapter.OrderListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int CURRENT_VIEW = 0;

        if (mOrderList.get(position) instanceof OrderHeader) {
            CURRENT_VIEW = ORDER_HEADER;
        } else if (mOrderList.get(position) instanceof OrderItem) {
            CURRENT_VIEW = ORDER_ITEM;
        }

        switch (CURRENT_VIEW) {
            case ORDER_HEADER:
                OrderListAdapter.OrderDetailsHeaderHolder orderHeaderHolder = (OrderDetailsHeaderHolder) viewHolder;
                OrderHeader orderHeader = (OrderHeader) mOrderList.get(position);
                orderHeaderHolder.mRank.setText(String.valueOf(orderHeader.getRank()));
                orderHeaderHolder.mTime.setText(orderHeader.getTime());
                break;

            case ORDER_ITEM:
                OrderListAdapter.OrderListViewHolder orderItemHolder = (OrderListViewHolder) viewHolder;
                OrderItem orderItem = (OrderItem) mOrderList.get(position);
                OrderStatus status = orderItem.getStatus();
                orderItemHolder.mName.setText(orderItem.getName());
                orderItemHolder.mItemCount.setText(String.valueOf(orderItem.getCount()));
                orderItemHolder.mItemTotal.setText(String.valueOf(orderItem.getTotal()));
                orderItemHolder.mStatus.setText(getStatusText(status));
                orderItemHolder.mStatus.setTextColor(ColorStateList.valueOf(mContext.getApplicationContext()
                        .getColor(getStatusColor(status))));
                orderItemHolder.mStatusImage.setImageResource(getStatusIcon(status));
                orderItemHolder.mStatusImage.setImageTintList(ColorStateList.valueOf(mContext.getApplicationContext()
                        .getColor(getStatusColor(status))));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }
}
