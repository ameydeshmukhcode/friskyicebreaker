package com.frisky.icebreaker.core.structures;

import java.io.Serializable;

public class MenuItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private int price;

    public MenuItem(String id, String name, String description, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }
}
