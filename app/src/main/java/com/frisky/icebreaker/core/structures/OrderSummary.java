package com.frisky.icebreaker.core.structures;

import android.net.Uri;

public class OrderSummary {
    private Uri restaurantImage;
    private String restaurantID;
    private String restaurantName;
    private String sessionID;
    private String endTime;

    private int totalAmount;

    public OrderSummary(String restaurantID, Uri restaurantImage, String restaurantName, String sessionID,
                        String endTime, int totalAmount) {
        this.restaurantImage = restaurantImage;
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.sessionID = sessionID;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
    }

    public Uri getRestaurantImage() {
        return restaurantImage;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getTotalAmount() {
        return totalAmount;
    }
}
