/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.game.ui.controls;

import java.util.ArrayList;
import java.util.List;

import de.projectsc.modes.client.core.ui.UIElement;

/**
 * Main menu of the game.
 *
 * @author Josch Bosch
 */
public class Menu extends UIElement {

    private List<String> menuItems = new ArrayList<>();

    public Menu() {
        super("Menu", 0);
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
