package com.frisky.icebreaker.core.structures;

import android.net.Uri;

import java.util.List;

public class Pub {
    private Uri imageUri;
    private String mID;
    private String mName;
    private String mDesc;
    private String mLocation;
    private List<String> mTags;
    private double mRating;
    
    public Pub() {
    }

    public Pub(Uri image, String id, String name, String desc, String location, List<String> tags, double rating) {
        this.imageUri = image;
        this.mID = id;
        this.mName = name;
        this.mDesc = desc;
        this.mLocation = location;
        this.mTags = tags;
        this.mRating = rating;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getID() {
        return mID;
    }

    public String getLocation() { return mLocation; }

    public void setLocation(String mLocation) { this.mLocation = mLocation; }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public List<String> getTags() {
        return mTags;
    }

    public void setTags(List<String> mTags) {
        this.mTags = mTags;
    }

    public double getRating() {
        return mRating;
    }

    public void setRating(float mRating) {
        this.mRating = mRating;
    }
}
