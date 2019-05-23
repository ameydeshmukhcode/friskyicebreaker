package com.frisky.icebreaker.core.structures.menu;

import java.util.List;

public class SubMenu {
    private String name;
    private List<MenuItem> itemList;

    public SubMenu(String name, List<MenuItem> itemList) {
        this.name = name;
        this.itemList = itemList;
    }

    public String getName() {
        return name;
    }
 
    public List<MenuItem> getItemList() {
        return itemList;
    }
}
