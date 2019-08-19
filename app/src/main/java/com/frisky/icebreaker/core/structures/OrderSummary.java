package com.frisky.icebreaker.core.structures;

public class OrderSummary {
    private String restaurantID;
    private String restaurantName;
    private String sessionID;
    private String endTime;

    public OrderSummary(String restaurantID, String restaurantName, String sessionID, String endTime) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.sessionID = sessionID;
        this.endTime = endTime;
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
}
