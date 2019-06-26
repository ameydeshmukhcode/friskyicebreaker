package com.frisky.icebreaker.orders;

public class OrderingAssistant {

    public static boolean SESSION_ACTIVE = false;

    private static final OrderingAssistant ourInstance = new OrderingAssistant();

    public static OrderingAssistant getInstance() {
        return ourInstance;
    }

    private OrderingAssistant() {
    }
}
