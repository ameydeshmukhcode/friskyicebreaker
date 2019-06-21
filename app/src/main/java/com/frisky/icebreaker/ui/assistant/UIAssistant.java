package com.frisky.icebreaker.ui.assistant;

import android.content.Context;
import android.graphics.Bitmap;

import com.frisky.icebreaker.R;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

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

    public File compressImage(File file, Context context) throws IOException {
        int height = 720, width = 720, quality = 25;

        return new Compressor(context)
                .setMaxHeight(height)
                .setMaxWidth(width)
                .setQuality(quality)
                .setCompressFormat(Bitmap.CompressFormat.PNG)
                .compressToFile(file);
    }
}
