package com.frisky.icebreaker.core.structures;

import android.net.Uri;

public class Restaurant {
    private Uri imageUri;
    private String id;
    private String name;
    private String location;
    private String cuisine;
    private double rating;
    
    public Restaurant() {
    }

    public Restaurant(Uri image, String id, String name, String location, String cuisine, double rating) {
        this.imageUri = image;
        this.id = id;
        this.name = name;
        this.location = location;
        this.cuisine = cuisine;
        this.rating = rating;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getID() {
        return id;
    }

    public String getLocation() { return location; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public double getRating() {
        return rating;
    }

}
