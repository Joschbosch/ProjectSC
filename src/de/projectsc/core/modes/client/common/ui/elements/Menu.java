/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.common.ui.elements;

import java.util.ArrayList;
import java.util.List;

import de.projectsc.core.modes.client.common.StateConstants;
import de.projectsc.core.modes.client.common.data.UIElement;

/**
 * Menu element in the main menu.
 * 
 * @author Josch Bosch
 */
public class Menu extends UIElement {

    private List<String> menuItems = new ArrayList<>();

    public Menu() {
        super(StateConstants.MENU, 0);
        menuItems.add("Play a Game");
        menuItems.add("Create a Game");
        menuItems.add("Quit");

    }

    public List<String> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<String> menuItems) {
        this.menuItems = menuItems;
    }
}
