package com.frisky.icebreaker.structures;

public class Pub {
    private String mName;
    private String mDesc;

    public Pub() {
    }

    public Pub(String mName, String mDesc) {
        this.mName = mName;
        this.mDesc = mDesc;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }
}
