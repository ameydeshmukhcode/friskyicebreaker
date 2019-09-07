package com.frisky.icebreaker.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.frisky.icebreaker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MenuViewAdapter extends PagerAdapter {

    private ArrayList<Uri> mMenuList;
    private Context mContext;

    public MenuViewAdapter(Context context, ArrayList<Uri> menuList) {
        mContext = context;
        mMenuList = menuList;
    }

    @Override
    public int getCount() {
        return mMenuList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.image_menu, container, false);
        ImageView image = layout.findViewById(R.id.image_menu_page);
        Picasso.get().load(mMenuList.get(position)).into(image);
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
