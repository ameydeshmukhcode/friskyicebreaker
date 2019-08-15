package com.frisky.icebreaker.interfaces;

import com.frisky.icebreaker.core.structures.MenuItem;

public interface OnOrderUpdateListener {
    void addToOrder(MenuItem item);
    void removeFromOrder(MenuItem item);
}
