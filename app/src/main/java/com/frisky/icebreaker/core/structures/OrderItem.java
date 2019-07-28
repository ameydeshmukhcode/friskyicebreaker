package com.frisky.icebreaker.core.structures;

public class OrderItem {
    private String id;
    private String name;
    private int count;
    private int total;

    public OrderItem(String id, String name, int count, int total) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
