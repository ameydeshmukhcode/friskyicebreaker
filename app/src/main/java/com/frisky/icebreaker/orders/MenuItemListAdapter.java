package com.frisky.icebreaker.orders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.menu.MenuItem;

import java.util.List;

public class MenuItemListAdapter extends RecyclerView.Adapter<MenuItemListAdapter.MenuItemHolder> {

    private List<MenuItem> menu;

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

    MenuItemListAdapter(List<MenuItem> menu) {
        this.menu = menu;
    }

    @NonNull
    @Override
    public MenuItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_menu_item, viewGroup, false);

        return new MenuItemListAdapter.MenuItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemHolder menuItemHolder, int i) {
        MenuItem menuItem = menu.get(i);
        menuItemHolder.mName.setText(menuItem.getName());
        menuItemHolder.mDescription.setText(menuItem.getDescription());
        menuItemHolder.mPrice.setText(String.valueOf(menuItem.getPrice()));
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }
}
