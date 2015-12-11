/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.ui.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import de.projectsc.modes.client.core.states.StateConstants;
import de.projectsc.modes.client.ui.BasicUIElement;

/**
 * Main menu of the game.
 *
 * @author Josch Bosch
 */
public class Menu extends BasicUIElement {

    private List<String> menuItems = new ArrayList<>();

    private int chosenItem;

    public Menu() {
        super(StateConstants.MENU, 0);
        menuItems.add("Play a Game");
        menuItems.add("Create a Game");
        menuItems.add("Quit");
        setChosenItem(0);
    }

    @Override
    public void handleInput(Map<Integer, Integer> keyMap) {
        if (keyMap.get(Keyboard.KEY_DOWN) != null
            && keyMap.get(Keyboard.KEY_DOWN) == 1) {
            chosenItem = ++chosenItem % menuItems.size();
        }
        if (keyMap.get(Keyboard.KEY_UP) != null
            && keyMap.get(Keyboard.KEY_UP) == 1) {
            --chosenItem;
            if (chosenItem == -1) {
                chosenItem = menuItems.size() - 1;
            }
        }
    }

    public List<String> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<String> menuItems) {
        this.menuItems = menuItems;
    }

    public String getBackground() {
        return "images/bg.png";
    }

    public int getChosenItem() {
        return chosenItem;
    }

    public void setChosenItem(int chosenItem) {
        this.chosenItem = chosenItem;
    }
}
