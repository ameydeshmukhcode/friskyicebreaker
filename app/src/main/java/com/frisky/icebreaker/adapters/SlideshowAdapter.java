package com.frisky.icebreaker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.frisky.icebreaker.R;

public class SlideshowAdapter extends PagerAdapter {

    private Context context;

    public SlideshowAdapter(Context applicationContext) {
        context = applicationContext;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.slideshow_layout, container, false);
        TextView title = layout.findViewById(R.id.text_slide_title);
        TextView desc = layout.findViewById(R.id.text_slide_desc);
        ImageView image = layout.findViewById(R.id.image_slideshow);
        switch (position) {
            case 0:
                title.setText("Scan QR");
                desc.setText("Scan QR codes on tables at any restaurant to start your dining experience.");
                image.setImageDrawable(context.getDrawable(R.drawable.slide_scan));
                break;
            case 1:
                title.setText("Instant Menu");
                desc.setText("Access the menu and browse your favorite dishes instantly.\n" +
                        "Place orders without having to wait.");
                image.setImageDrawable(context.getDrawable(R.drawable.slide_menu));
                break;
            case 2:
                title.setText("Keep Tabs");
                desc.setText("Get real-time bill and order updates and check your previous orders at anytime.");
                image.setImageDrawable(context.getDrawable(R.drawable.slide_summary));
                break;
        }
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
