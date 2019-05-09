package com.frisky.icebreaker.core.store;

import com.frisky.icebreaker.R;

public class UserDataStore {
    private static final UserDataStore ourInstance = new UserDataStore();

    public static UserDataStore getInstance() {
        return ourInstance;
    }

    private UserDataStore() {
    }

    public int[] getImageList() {
        return mImageList;
    }

    public void setImageList(int[] mImageList) {
        this.mImageList = mImageList;
    }

    int[] mImageList = {
            R.drawable.placeholder,
            R.drawable.placeholder,
            R.drawable.placeholder,
            R.drawable.placeholder,
            R.drawable.placeholder,
            R.drawable.placeholder
    };

    public int getImageCount() {
        return IMAGE_COUNT;
    }

    private final int IMAGE_COUNT = 6;
}
