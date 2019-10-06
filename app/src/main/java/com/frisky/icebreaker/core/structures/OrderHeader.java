package com.frisky.icebreaker.core.structures;

public class OrderHeader {
    private String time;
    private int rank;

    public OrderHeader(String time, int rank) {
        this.time = time;
        this.rank = rank;
    }

    public String getTime() {
        return time;
    }

    public int getRank() {
        return rank;
    }
}
