package com.frisky.icebreaker.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.core.structures.MutableInt;

import java.util.HashMap;
import java.util.Objects;

public class CartListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private HashMap<MenuItem, MutableInt> mCartList;
    private OnOrderUpdateListener orderUpdateListener;

    CartListAdapter(Context context, HashMap<MenuItem, MutableInt> cartList, OnOrderUpdateListener listener) {
        this.mContext = context;
        this.mCartList = cartList;
        this.orderUpdateListener = listener;
    }

    static class CartListViewHolder extends RecyclerView.ViewHolder {
        ImageButton mAdd;
        ImageButton mRemove;
        TextView mName;
        TextView mPrice;
        TextView mStatus;
        TextView mCount;
        TextView mItemTotal;
        CartListViewHolder(@NonNull View view) {
            super(view);
            mName = view.findViewById(R.id.text_name);
            mPrice = view.findViewById(R.id.text_price);
            mStatus = view.findViewById(R.id.in_cart);
            mCount = view.findViewById(R.id.text_item_count);
            mItemTotal = view.findViewById(R.id.text_cart_item_total);
            mAdd = view.findViewById(R.id.button_add);
            mRemove = view.findViewById(R.id.button_remove);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_cart_item, viewGroup, false);

        return new CartListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        CartListViewHolder cartItemHolder = (CartListViewHolder) viewHolder;
        MenuItem item = (MenuItem) Objects.requireNonNull(mCartList.keySet().toArray())[position];
        MutableInt count = (MutableInt) mCartList.values().toArray()[position];
        cartItemHolder.mName.setText(item.getName());
        cartItemHolder.mCount.setText(String.valueOf(count.getValue()));
        cartItemHolder.mPrice.setText(String.valueOf(item.getPrice()));
        cartItemHolder.mItemTotal.setText(String.valueOf(item.getPrice() * count.getValue()));
        cartItemHolder.mAdd.setOnClickListener(v -> {
            int countInc = Integer.parseInt(cartItemHolder.mCount.getText().toString()) + 1;
            cartItemHolder.mCount.setText(String.valueOf(countInc));
            orderUpdateListener.addToOrder(item);
        });
        cartItemHolder.mRemove.setOnClickListener(v -> {
            int countMod = Integer.parseInt(cartItemHolder.mCount.getText().toString());
            if (countMod > 0) {
                int countDec = Integer.parseInt(cartItemHolder.mCount.getText().toString()) - 1;
                cartItemHolder.mCount.setText(String.valueOf(countDec));
                orderUpdateListener.removeFromOrder(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }
}
