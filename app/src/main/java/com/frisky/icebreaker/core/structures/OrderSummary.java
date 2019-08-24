package com.frisky.icebreaker.core.structures;

public class OrderSummary {
    private String restaurantID;
    private String restaurantName;
    private String sessionID;
    private String endTime;

    private int totalAmount;

    public OrderSummary(String restaurantID, String restaurantName, String sessionID, String endTime, int totalAmount) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.sessionID = sessionID;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
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
