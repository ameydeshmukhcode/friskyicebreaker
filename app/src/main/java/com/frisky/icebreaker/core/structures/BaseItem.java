package com.frisky.icebreaker.core.structures;

import java.io.Serializable;

public abstract class BaseItem implements Serializable {
    protected String id;
    protected String name;
    int count = 0;
}
