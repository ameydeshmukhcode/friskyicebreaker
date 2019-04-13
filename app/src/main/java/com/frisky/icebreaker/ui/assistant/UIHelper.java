package com.frisky.icebreaker.ui.assistant;

import com.frisky.icebreaker.R;

public class UIHelper {
    private static final UIHelper ourInstance = new UIHelper();

    public static UIHelper getInstance() {
        return ourInstance;
    }

    private UIHelper() {
    }

    public int getRatingBadgeBackground(double rating) {
        if (rating >= 4.0) {
            return R.drawable.pub_rating_very_high;
        }
        else if (rating >= 3.5) {
            return R.drawable.pub_rating_high;
        }
        else if (rating >= 2.5) {
            return R.drawable.pub_rating_low;
        }
        else {
            return R.drawable.pub_rating_very_low;
        }
    }

    public int getRatingBadgeColor(double rating) {
        if (rating >= 4.0) {
            return R.color.rating_very_high;
        }
        else if (rating >= 3.5) {
            return R.color.rating_high;
        }
        else if (rating >= 2.5) {
            return R.color.rating_low;
        }
        else {
            return R.color.rating_very_low;
        }
    }
}
