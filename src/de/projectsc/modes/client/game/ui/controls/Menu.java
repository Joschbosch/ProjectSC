/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.game.ui.controls;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import de.projectsc.core.data.KeyboardInputCommand;
import de.projectsc.core.data.MouseInputCommand;
import de.projectsc.core.interfaces.InputCommandListener;
import de.projectsc.modes.client.core.ui.UIElement;

/**
 * Main menu of the game.
 *
 * @author Josch Bosch
 */
public class Menu extends UIElement implements InputCommandListener {

    private List<String> menuItems = new ArrayList<>();

    private int chosenItem;

    public Menu() {
        super("Menu", 0);
        menuItems.add("Play a Game");
        menuItems.add("Create a Game");
        menuItems.add("Quit");
        setChosenItem(0);
    }

    public List<String> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<String> menuItems) {
        this.menuItems = menuItems;
    }

    public int getChosenItem() {
        return chosenItem;
    }

    public void setChosenItem(int chosenItem) {
        this.chosenItem = chosenItem;
    }

    @Override
    public InputConsumeLevel getInputConsumeLevel() {
        return InputConsumeLevel.SECOND;
    }

    @Override
    public void handleKeyboardCommand(KeyboardInputCommand command) {
        if (command.isKeyDown() && !command.isKeyRepeated()) {
            if (command.getKey() == Keyboard.KEY_DOWN) {
                chosenItem = (chosenItem + 1) % menuItems.size();
            }
        }
        if (command.isKeyDown() && !command.isKeyRepeated()) {
            if (command.getKey() == Keyboard.KEY_UP) {
                if (chosenItem > 0) {
                    chosenItem--;
                } else {
                    chosenItem = menuItems.size() - 1;
                }
            }
        }
    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {

    }
}
