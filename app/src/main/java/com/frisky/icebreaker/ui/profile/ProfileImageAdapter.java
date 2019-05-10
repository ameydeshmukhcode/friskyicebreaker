package com.frisky.icebreaker.ui.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.assistant.UIAssistant;
import com.frisky.icebreaker.core.store.*;

public class ProfileImageAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    public ProfileImageAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return UserDataStore.getInstance().getImageList().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.view_image, container, false);

        ImageView imageView = itemView.findViewById(R.id.image_placeholder);
        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(),  UserDataStore.getInstance().getImageList()[position]);
        imageView.setImageBitmap(UIAssistant.getInstance().getProfileBitmap(bm));

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}
