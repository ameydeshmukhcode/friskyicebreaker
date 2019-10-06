package com.frisky.icebreaker.core.structures;

public class OrderItem extends BaseItem {
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

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public int getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
