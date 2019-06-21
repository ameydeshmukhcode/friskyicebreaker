package com.frisky.icebreaker.ui.assistant;

import com.frisky.icebreaker.R;

public class UIAssistant {
    private static final UIAssistant ourInstance = new UIAssistant();

    public static UIAssistant getInstance() {
        return ourInstance;
    }

    private UIAssistant() {
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
