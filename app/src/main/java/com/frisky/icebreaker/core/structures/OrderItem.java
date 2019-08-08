package com.frisky.icebreaker.core.structures;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String id;
    private String name;
    private int count;
    private int total;
    private OrderStatus status = OrderStatus.PENDING;

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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
