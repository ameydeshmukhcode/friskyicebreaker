package com.frisky.icebreaker.core.structures;

public class MutableInt {
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
