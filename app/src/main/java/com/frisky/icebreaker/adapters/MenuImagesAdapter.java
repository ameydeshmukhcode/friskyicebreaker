package com.frisky.icebreaker.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.activities.MenuViewActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MenuImagesAdapter extends RecyclerView.Adapter<MenuImagesAdapter.ImageViewHolder> {

    private ArrayList<Uri> mMenuList;
    private Context context;

    public MenuImagesAdapter(Context applicationContext, ArrayList<Uri> list) {
        this.context = applicationContext;
        this.mMenuList = list;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView menuImage;
        ImageViewHolder(View view) {
            super(view);
            menuImage = view.findViewById(R.id.image_menu_preview);
        }
    }

    @NonNull
    @Override
    public MenuImagesAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int viewType) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.image_menu_preview, viewGroup, false);

        return new MenuImagesAdapter.ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Picasso.get().load(mMenuList.get(position)).into(holder.menuImage);
        holder.menuImage.setOnClickListener(v -> {
            Intent showMenu = new Intent(context.getApplicationContext(), MenuViewActivity.class);
            showMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            showMenu.putParcelableArrayListExtra("menu_list", mMenuList);
            showMenu.putExtra("menu_page", position);
            context.getApplicationContext().startActivity(showMenu);
        });
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }
}
