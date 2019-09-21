package com.frisky.icebreaker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.DietType;
import com.frisky.icebreaker.core.structures.MenuCategory;
import com.frisky.icebreaker.core.structures.MenuItem;
import com.frisky.icebreaker.interfaces.OnOrderUpdateListener;

import java.util.List;

import static com.frisky.icebreaker.ui.assistant.UIAssistant.getTypeIcon;

public class MenuItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> mMenu;

    private final int CATEGORY_VIEW = 77;
    private final int MENU_ITEM_VIEW = 88;

    private OnOrderUpdateListener orderUpdateListener;

    static class MenuItemHolder extends RecyclerView.ViewHolder {
        ImageButton mAdd;
        ImageButton mRemove;
        ImageView mDietTypeIcon;
        TextView mName;
        TextView mDescription;
        TextView mPrice;
        TextView mCount;
        TextView mAvailable;
        TextView mAddText;
        MenuItemHolder(View view) {
            super(view);
            mAdd = view.findViewById(R.id.button_add);
            mRemove = view.findViewById(R.id.button_remove);
            mDietTypeIcon = view.findViewById(R.id.image_diet_type);
            mName = view.findViewById(R.id.text_name);
            mDescription = view.findViewById(R.id.text_description);
            mPrice = view.findViewById(R.id.text_price);
            mCount = view.findViewById(R.id.text_item_count);
            mAvailable = view.findViewById(R.id.text_available);
            mAddText = view.findViewById(R.id.add_item);
        }
    }

    static class MenuSubCategoryHolder extends RecyclerView.ViewHolder {
        TextView mName;
        MenuSubCategoryHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.text_category_name);
        }
    }

    public MenuItemListAdapter(List<Object> menu, OnOrderUpdateListener listener) {
        this.mMenu = menu;
        this.orderUpdateListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMenu.get(position) instanceof MenuCategory) {
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

        if (mMenu.get(position) instanceof MenuCategory) {
            CURRENT_VIEW = CATEGORY_VIEW;
        }
        else if (mMenu.get(position) instanceof MenuItem) {
            CURRENT_VIEW = MENU_ITEM_VIEW;
        }

        switch (CURRENT_VIEW) {
            case CATEGORY_VIEW:
                MenuSubCategoryHolder view = (MenuSubCategoryHolder) viewHolder;
                view.mName.setText(((MenuCategory) mMenu.get(position)).getName());
                break;

            case MENU_ITEM_VIEW:
                MenuItemHolder itemHolder = (MenuItemHolder) viewHolder;
                MenuItem menuItem = (MenuItem) mMenu.get(position);
                boolean available = menuItem.getAvailable();

                if (menuItem.getCount() > 0) {
                    itemHolder.mAddText.setVisibility(View.INVISIBLE);
                    itemHolder.mRemove.setVisibility(View.VISIBLE);
                    itemHolder.mCount.setVisibility(View.VISIBLE);
                    itemHolder.mCount.setText(String.valueOf(menuItem.getCount()));
                }
                else {
                    itemHolder.mAddText.setVisibility(View.VISIBLE);
                    itemHolder.mRemove.setVisibility(View.INVISIBLE);
                    itemHolder.mCount.setVisibility(View.INVISIBLE);
                }

                if (!available) {
                    itemHolder.mAdd.setEnabled(false);
                    itemHolder.mAddText.setEnabled(false);
                    itemHolder.mAvailable.setText(R.string.unavailable);
                }

                itemHolder.mName.setText(menuItem.getName());
                itemHolder.mDescription.setText(menuItem.getDescription());
                itemHolder.mPrice.setText(String.valueOf(menuItem.getPrice()));
                itemHolder.mDietTypeIcon.setImageResource(getTypeIcon(menuItem.getDietType()));

                itemHolder.mAddText.setOnClickListener(v -> {
                    menuItem.incrementCount();
                    itemHolder.mCount.setText(String.valueOf(menuItem.getCount()));
                    itemHolder.mAddText.setVisibility(View.INVISIBLE);
                    itemHolder.mRemove.setVisibility(View.VISIBLE);
                    itemHolder.mCount.setVisibility(View.VISIBLE);
                    orderUpdateListener.addToOrder(menuItem);
                });

                itemHolder.mAdd.setOnClickListener(v -> {
                    menuItem.incrementCount();
                    itemHolder.mCount.setText(String.valueOf(menuItem.getCount()));
                    itemHolder.mAddText.setVisibility(View.INVISIBLE);
                    itemHolder.mRemove.setVisibility(View.VISIBLE);
                    itemHolder.mCount.setVisibility(View.VISIBLE);
                    orderUpdateListener.addToOrder(menuItem);
                });
                
                itemHolder.mRemove.setOnClickListener(v -> {
                    menuItem.decrementCount();
                    itemHolder.mCount.setText(String.valueOf(menuItem.getCount()));
                    orderUpdateListener.removeFromOrder(menuItem);
                    if (menuItem.getCount() == 0) {
                        itemHolder.mAddText.setVisibility(View.VISIBLE);
                        itemHolder.mRemove.setVisibility(View.INVISIBLE);
                        itemHolder.mCount.setVisibility(View.INVISIBLE);
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
