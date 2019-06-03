package com.frisky.icebreaker.profile;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.components.dialogs.PickImageDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditImagesAdapter extends RecyclerView.Adapter<EditImagesAdapter.ImageViewHolder> {

    private Context mContext;
    private FragmentActivity mActivity;
    private PickImageDialog pickImageDialog;

    List<Uri> mImageList = new ArrayList<>();

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        ImageViewHolder(View v) {
            super(v);
            mImage = v.findViewById(R.id.image_edit);
        }
    }

    EditImagesAdapter(Context context, FragmentActivity activity) {
        mContext = context;
        mActivity = activity;
    }

    @NonNull
    @Override
    public EditImagesAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_edit_image, viewGroup, false);

        ImageView imageView = itemView.findViewById(R.id.image_edit);
        Picasso.get().load(mImageList.get(i)).into(imageView);

        return new ImageViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull final ImageViewHolder viewHolder, int i) {
        viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageDialog = new PickImageDialog();
                pickImageDialog.show(mActivity.getSupportFragmentManager(), "pick image dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }
}
