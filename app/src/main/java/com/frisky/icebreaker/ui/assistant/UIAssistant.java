package com.frisky.icebreaker.ui.assistant;

import android.content.Context;
import android.graphics.Bitmap;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.DietType;
import com.frisky.icebreaker.core.structures.OrderStatus;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class UIAssistant {

    public static int getRatingBadgeColor(double rating) {
        if (rating >= 4.0) {
            return R.color.rating_very_high;
        }
        else if (rating >= 3.0) {
            return R.color.rating_high;
        }
        else if (rating >= 2.0) {
            return R.color.rating_low;
        }
        else {
            return R.color.rating_very_low;
        }
    }

    public static int getStatusColor(OrderStatus status) {
        switch (status) {
            case PENDING: return R.color.rating_low;
            case ACCEPTED: return R.color.rating_high;
            case REJECTED:
            case CANCELLED: return R.color.rating_very_low;
        }

        return R.color.rating_very_low;
    }

    public static int getStatusIcon(OrderStatus status) {
        switch (status) {
            case PENDING: return R.drawable.ic_status_pending;
            case ACCEPTED: return R.drawable.ic_status_accepted;
            case REJECTED:
            case CANCELLED: return R.drawable.ic_status_rejected;
        }

        return R.drawable.ic_status_rejected;
    }

    public static String getStatusText(OrderStatus status) {
        switch (status) {
            case PENDING: return "Pending";
            case ACCEPTED: return "Accepted";
            case REJECTED: return "Rejected";
            case CANCELLED: return "Cancelled";
        }

        return "";
    }

    public static int getTypeIcon(DietType type) {
        switch (type) {
            case NONE: return R.drawable.bg_badge;
            case VEG: return R.drawable.ic_veg;
            case NON_VEG: return R.drawable.ic_non_veg;
            case EGG: return R.drawable.ic_egg;
        }

        return R.drawable.ic_veg;
    }

    public static DietType getDietTypeFromString(String type) {
        switch (type) {
            case "NONE": return DietType.NONE;
            case "VEG": return DietType.VEG;
            case "NON_VEG": return DietType.NON_VEG;
            case "CONTAINS_EGG": return DietType.EGG;
        }

        return DietType.NONE;
    }

    public static File compressImage(File file, Context context) throws IOException {
        int height = 720, width = 720, quality = 50;

        return new Compressor(context)
                .setMaxHeight(height)
                .setMaxWidth(width)
                .setQuality(quality)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFile(file);
    }
}
