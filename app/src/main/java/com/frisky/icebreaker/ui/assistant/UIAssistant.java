package com.frisky.icebreaker.ui.assistant;

import android.content.Context;
import android.graphics.Bitmap;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.OrderStatus;

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

    public int getStatusColor(OrderStatus status) {
        switch (status) {
            case PENDING: return R.color.rating_low;
            case ACCEPTED: return R.color.rating_high;
            case REJECTED:
            case CANCELLED: return R.color.rating_very_low;
        }

        return R.color.rating_very_low;
    }

    public int getStatusIcon(OrderStatus status) {
        switch (status) {
            case PENDING: return R.drawable.round_pending_24;
            case ACCEPTED: return R.drawable.round_accepted_24;
            case REJECTED:
            case CANCELLED: return R.drawable.round_rejected_24;
        }

        return R.drawable.round_rejected_24;
    }

    public String getStatusText(OrderStatus status) {
        switch (status) {
            case PENDING: return "Pending";
            case ACCEPTED: return "Accepted";
            case REJECTED: return "Rejected";
            case CANCELLED: return "Cancelled";
        }

        return "";
    }

    public File compressImage(File file, Context context) throws IOException {
        int height = 720, width = 720, quality = 50;

        return new Compressor(context)
                .setMaxHeight(height)
                .setMaxWidth(width)
                .setQuality(quality)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFile(file);
    }
}
