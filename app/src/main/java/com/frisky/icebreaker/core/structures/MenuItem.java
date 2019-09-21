package com.frisky.icebreaker.core.structures;

import java.io.Serializable;

import static com.frisky.icebreaker.core.structures.DietType.NONE;

public class MenuItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private int price;
    private boolean available;
    private int count = 0;
    private DietType dietType = NONE;

    public MenuItem(String id, String name, String description, int price, boolean available, DietType dietType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
        this.dietType = dietType;
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

    public boolean getAvailable() {
        return available;
    }

    public DietType getDietType() {
        return dietType;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        ++count;
    }

    public void decrementCount() {
        --count;
    }
}
