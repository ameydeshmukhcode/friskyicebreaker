package com.frisky.icebreaker.core.structures;

import java.util.Date;

public class User {
    private String mID;
    private String mName;
    private String mBio;
    private int mAge;
    private Date mDateOfBirth;
    public User() {
    }

    public User(String id, String mName, String mBio, int mAge, Date mDateOfBirth) {
        this.mID = id;
        this.mName = mName;
        this.mBio = mBio;
        this.mAge = mAge;
        this.mDateOfBirth = mDateOfBirth;
    }

    public String getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getBio() {
        return mBio;
    }

    public void setBio(String mBio) {
        this.mBio = mBio;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int mAge) {
        this.mAge = mAge;
    }

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(Date mDateOfBirth) {
        this.mDateOfBirth = mDateOfBirth;
    }
}
