package com.frisky.icebreaker.core.structures;

import java.util.Date;

public class User {
    private String id;
    private String name;
    private String bio;
    private int age;
    private Date dateOfBirth;
    public User() {
    }

    public User(String id, String name, String bio, int age, Date dateOfBirth) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
