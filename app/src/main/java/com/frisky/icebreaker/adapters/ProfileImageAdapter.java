package com.frisky.icebreaker.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.assistant.RoundRectTransformation;
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
    public Object instantiateItem(@NonNull ViewGroup viewGroup, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.view_image, viewGroup, false);

        ImageView imageView = itemView.findViewById(R.id.image_placeholder);
        Picasso.get().load(mImageList.get(position)).transform(new RoundRectTransformation()).into(imageView);
        viewGroup.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup viewGroup, int position, @NonNull Object object) {
        viewGroup.removeView((ImageView) object);
    }

    public void addToImageList(Uri image) {
        mImageList.add(image);
    }
}
