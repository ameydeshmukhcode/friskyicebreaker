package com.frisky.icebreaker.core.structures.menu;

import java.util.List;

public class Menu {
    private List<SubMenu> subMenuList;

    public Menu(List<SubMenu> subMenuList) {
        this.subMenuList = subMenuList;
    }

    public List<SubMenu> getSubMenuList() {
        return subMenuList;
    }
}
