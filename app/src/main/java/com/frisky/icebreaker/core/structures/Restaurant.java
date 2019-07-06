package com.frisky.icebreaker.core.structures;

import android.net.Uri;

public class Restaurant {
    private Uri imageUri;
    private String id;
    private String name;
    private String description;
    private String location;
    private String cuisine;
    private double rating;
    
    public Restaurant() {
    }

    public Restaurant(Uri image, String id, String name, String desc, String location, String cuisine, double rating) {
        this.imageUri = image;
        this.id = id;
        this.name = name;
        this.description = desc;
        this.location = location;
        this.cuisine = cuisine;
        this.rating = rating;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getID() {
        return id;
    }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return description;
    }

    public void setDesc(String description) {
        this.description = description;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
