package com.frisky.icebreaker.ui.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.store.UserDataStore;
import com.frisky.icebreaker.ui.assistant.UIAssistant;

public class EditImagesAdapter extends RecyclerView.Adapter<EditImagesAdapter.ImageViewHolder> {

    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        ImageViewHolder(View v) {
            super(v);
            mImage = v.findViewById(R.id.image_edit);
        }
    }

    EditImagesAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public EditImagesAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_edit_image, viewGroup, false);

        ImageView imageView = itemView.findViewById(R.id.image_edit);
        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), UserDataStore.getInstance().getImageList()[i]);
        imageView.setImageBitmap(UIAssistant.getInstance().getProfileBitmap(bm));

        return new ImageViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull final ImageViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return UserDataStore.getInstance().getImageCount();
    }
}
