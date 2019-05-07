package com.frisky.icebreaker.ui.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.assistant.UIAssistant;

public class ProfileImageAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    int[] mImageList = {
            R.drawable.logo_facebook,
            R.drawable.placeholder,
            R.drawable.logo_facebook,
            R.drawable.placeholder,
            R.drawable.logo_facebook
    };

    public ProfileImageAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mImageList.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_images, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), mImageList[position]);
        imageView.setImageBitmap(UIAssistant.getInstance().getProfileBitmap(bm));

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
