package com.frisky.icebreaker.orders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.MenuItem;

import java.util.HashMap;
import java.util.List;

public class MenuItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> menu;
    private HashMap<String, String> categories;

    private final int CATEGORY_VIEW = 77;
    private final int MENU_ITEM_VIEW = 88;

    private OnOrderListChangeListener orderListChangeListener;

    static class MenuItemHolder extends RecyclerView.ViewHolder {
        ImageButton mAdd;
        ImageButton mRemove;
        TextView mName;
        TextView mDescription;
        TextView mPrice;
        TextView mCount;
        MenuItemHolder(View v) {
            super(v);
            mAdd = v.findViewById(R.id.button_add);
            mRemove = v.findViewById(R.id.button_remove);
            mName = v.findViewById(R.id.text_name);
            mDescription = v.findViewById(R.id.text_description);
            mPrice = v.findViewById(R.id.text_price);
            mCount = v.findViewById(R.id.text_item_count);
        }
    }

    static class MenuSubCategoryHolder extends RecyclerView.ViewHolder {
        TextView mName;
        MenuSubCategoryHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.text_category_name);
        }
    }

    MenuItemListAdapter(List<Object> menu, HashMap<String, String> categories, OnOrderListChangeListener listener) {
        this.menu = menu;
        this.categories = categories;
        this.orderListChangeListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (menu.get(position) instanceof String) {
            return CATEGORY_VIEW;
        }
        else if (menu.get(position) instanceof MenuItem) {
            return MENU_ITEM_VIEW;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView;
        switch (i) {
            case CATEGORY_VIEW:
                itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_menu_category, viewGroup, false);

                return new MenuItemListAdapter.MenuSubCategoryHolder(itemView);

            case MENU_ITEM_VIEW:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.card_menu_item, viewGroup, false);

                return new MenuItemListAdapter.MenuItemHolder(itemView);
        }

        itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_menu_category, viewGroup, false);

        return new MenuItemListAdapter.MenuItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        int CURRENT_VIEW = 0;

        if (menu.get(i) instanceof String) {
            CURRENT_VIEW = CATEGORY_VIEW;
        }
        else if (menu.get(i) instanceof MenuItem) {
            CURRENT_VIEW = MENU_ITEM_VIEW;
        }

        switch (CURRENT_VIEW) {
            case CATEGORY_VIEW:
                MenuSubCategoryHolder view = (MenuSubCategoryHolder) viewHolder;
                view.mName.setText(categories.get(menu.get(i).toString()));
                break;

            case MENU_ITEM_VIEW:
                MenuItemHolder itemHolder = (MenuItemHolder) viewHolder;
                MenuItem menuItem = (MenuItem) menu.get(i);
                itemHolder.mName.setText(menuItem.getName());
                itemHolder.mDescription.setText(menuItem.getDescription());
                itemHolder.mPrice.setText(String.valueOf(menuItem.getPrice()));
                itemHolder.mAdd.setOnClickListener(v -> {
                    int countInc = Integer.parseInt(itemHolder.mCount.getText().toString()) + 1;
                    itemHolder.mCount.setText(String.valueOf(countInc));
                    orderListChangeListener.addToOrder(menuItem);
                });
                itemHolder.mRemove.setOnClickListener(v -> {
                    if (Integer.parseInt(itemHolder.mCount.getText().toString()) > 0) {
                        int countDec = Integer.parseInt(itemHolder.mCount.getText().toString()) - 1;
                        itemHolder.mCount.setText(String.valueOf(countDec));
                        orderListChangeListener.removeFromOrder(menuItem);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    interface OnOrderListChangeListener {
        void addToOrder(MenuItem item);
        void removeFromOrder(MenuItem item);
    }
}
