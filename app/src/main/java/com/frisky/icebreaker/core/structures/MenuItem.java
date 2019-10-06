package com.frisky.icebreaker.core.structures;

public class MenuItem extends BaseItem {
    private String description;
    private String category;
    private int price;
    private boolean available;
    private DietType dietType;

    public MenuItem(String id, String name, String description, String category, int price, boolean available, DietType dietType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
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

    public String getCategory() {
        return category;
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
