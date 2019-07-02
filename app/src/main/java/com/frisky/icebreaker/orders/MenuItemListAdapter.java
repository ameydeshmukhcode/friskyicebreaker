package com.frisky.icebreaker.orders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.menu.MenuItem;

import java.util.List;

public class MenuItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> menu;

    private final int CATEGORY_VIEW = 77;
    private final int MENU_ITEM_VIEW = 88;

    static class MenuItemHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mDescription;
        TextView mPrice;
        MenuItemHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.text_name);
            mDescription = v.findViewById(R.id.text_description);
            mPrice = v.findViewById(R.id.text_price);
        }
    }

    static class MenuSubCategoryHolder extends RecyclerView.ViewHolder {
        TextView mName;
        MenuSubCategoryHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.text_category_name);
        }
    }

    MenuItemListAdapter(List<Object> menu) {
        this.menu = menu;
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
                Log.i("data", (String) menu.get(i));
                MenuSubCategoryHolder view = (MenuSubCategoryHolder) viewHolder;
                view.mName.setText((String) menu.get(i));
                break;

            case MENU_ITEM_VIEW:
                MenuItemHolder itemHolder = (MenuItemHolder) viewHolder;
                MenuItem menuItem = (MenuItem) menu.get(i);
                Log.i("data", menuItem.getName() + " " + menuItem.getPrice());
                itemHolder.mName.setText(menuItem.getName());
                itemHolder.mDescription.setText(menuItem.getDescription());
                itemHolder.mPrice.setText(String.valueOf(menuItem.getPrice()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }
}
