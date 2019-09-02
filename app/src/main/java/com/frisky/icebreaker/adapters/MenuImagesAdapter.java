package com.frisky.icebreaker.adapters;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MenuImagesAdapter extends RecyclerView.Adapter<MenuImagesAdapter.ImageViewHolder> {

    List<Uri> mMenuList = new ArrayList<>();
    Context context;

    public MenuImagesAdapter(Context applicationContext, List<Uri> list) {
        this.context = applicationContext;
        this.mMenuList = list;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView menuImage;
        ImageViewHolder(View view) {
            super(view);
            menuImage = view.findViewById(R.id.image_edit);
        }
    }

    @NonNull
    @Override
    public MenuImagesAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int viewType) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_edit_image, viewGroup, false);

        return new MenuImagesAdapter.ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Picasso.get().load(mMenuList.get(position)).into(holder.menuImage);
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }
}
