package com.frisky.icebreaker.core.structures;

import java.io.Serializable;

public class MutableInt implements Serializable {
    private int value = 1;
    public void increment() {
        ++value;
    }
    public void decrement() {
        --value;
    }
    public int getValue() {
        return value;
    }
}
