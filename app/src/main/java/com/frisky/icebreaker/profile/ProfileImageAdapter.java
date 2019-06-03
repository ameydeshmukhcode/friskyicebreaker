package com.frisky.icebreaker.profile;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.frisky.icebreaker.R;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileImageAdapter extends PagerAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Uri> mImageList = new ArrayList<>();

    public ProfileImageAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.view_image, container, false);

        ImageView imageView = itemView.findViewById(R.id.image_placeholder);
        Picasso.get().load(mImageList.get(position)).into(imageView);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }

    public void addToImageList(Uri image) {
        mImageList.add(image);
    }
}
