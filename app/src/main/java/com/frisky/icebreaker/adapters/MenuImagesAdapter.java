package com.frisky.icebreaker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;

public class MenuImagesAdapter extends RecyclerView.Adapter<MenuImagesAdapter.ImageViewHolder> {
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageViewHolder(View view) {
            super(view);
        }
    }

    @NonNull
    @Override
    public MenuImagesAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int viewType) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_edit_image, viewGroup, false);

        ImageView imageView = itemView.findViewById(R.id.image_edit);
        imageView.setImageResource(R.drawable.placeholder);

        return new MenuImagesAdapter.ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
