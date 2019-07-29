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

    private List<Object> mMenu;
    private HashMap<String, String> mCategories;

    private final int CATEGORY_VIEW = 77;
    private final int MENU_ITEM_VIEW = 88;

    private OnOrderUpdateListener orderUpdateListener;

    static class MenuItemHolder extends RecyclerView.ViewHolder {
        ImageButton mAdd;
        ImageButton mRemove;
        TextView mName;
        TextView mDescription;
        TextView mPrice;
        TextView mCount;
        TextView mAvailable;
        MenuItemHolder(View view) {
            super(view);
            mAdd = view.findViewById(R.id.button_add);
            mRemove = view.findViewById(R.id.button_remove);
            mName = view.findViewById(R.id.text_name);
            mDescription = view.findViewById(R.id.text_description);
            mPrice = view.findViewById(R.id.text_price);
            mCount = view.findViewById(R.id.text_item_count);
            mAvailable = view.findViewById(R.id.text_available);
        }
    }

    static class MenuSubCategoryHolder extends RecyclerView.ViewHolder {
        TextView mName;
        MenuSubCategoryHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.text_category_name);
        }
    }

    MenuItemListAdapter(List<Object> menu, HashMap<String, String> categories, OnOrderUpdateListener listener) {
        this.mMenu = menu;
        this.mCategories = categories;
        this.orderUpdateListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMenu.get(position) instanceof String) {
            return CATEGORY_VIEW;
        }
        else if (mMenu.get(position) instanceof MenuItem) {
            return MENU_ITEM_VIEW;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;
        switch (viewType) {
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        int CURRENT_VIEW = 0;

        if (mMenu.get(position) instanceof String) {
            CURRENT_VIEW = CATEGORY_VIEW;
        }
        else if (mMenu.get(position) instanceof MenuItem) {
            CURRENT_VIEW = MENU_ITEM_VIEW;
        }

        switch (CURRENT_VIEW) {
            case CATEGORY_VIEW:
                MenuSubCategoryHolder view = (MenuSubCategoryHolder) viewHolder;
                view.mName.setText(mCategories.get(mMenu.get(position).toString()));
                break;

            case MENU_ITEM_VIEW:
                MenuItemHolder itemHolder = (MenuItemHolder) viewHolder;
                MenuItem menuItem = (MenuItem) mMenu.get(position);
                boolean available = menuItem.getAvailable();

                if (!available) {
                    itemHolder.mAdd.setEnabled(false);
                    itemHolder.mRemove.setEnabled(false);
                    itemHolder.mAvailable.setText(R.string.unavailable);
                }

                itemHolder.mName.setText(menuItem.getName());
                itemHolder.mDescription.setText(menuItem.getDescription());
                itemHolder.mPrice.setText(String.valueOf(menuItem.getPrice()));
                itemHolder.mAdd.setOnClickListener(v -> {
                    int countInc = Integer.parseInt(itemHolder.mCount.getText().toString()) + 1;
                    itemHolder.mCount.setText(String.valueOf(countInc));
                    orderUpdateListener.addToOrder(menuItem);
                });
                itemHolder.mRemove.setOnClickListener(v -> {
                    int count = Integer.parseInt(itemHolder.mCount.getText().toString());
                    if (count > 0) {
                        int countDec = Integer.parseInt(itemHolder.mCount.getText().toString()) - 1;
                        itemHolder.mCount.setText(String.valueOf(countDec));
                        orderUpdateListener.removeFromOrder(menuItem);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMenu.size();
    }
}
