/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.core.states;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import de.projectsc.modes.client.interfaces.ClientState;
import de.projectsc.modes.client.interfaces.GUI;
import de.projectsc.modes.client.messages.ClientMessage;
import de.projectsc.modes.client.ui.BasicUIElement;
import de.projectsc.modes.client.ui.elements.Console;
import de.projectsc.modes.client.ui.elements.Menu;

/**
 * The menu is the state when no game is running. The player can change options, create or join
 * games or do many more things.
 * 
 * @author Josch Bosch
 */
public class MenuState implements ClientState {

    private static final int STATE_LOGIN = 0;

    private static final int STATE_MAIN_MENU = 1;

    private static final int STATE_CREATE_GAME_MENU = 2;

    private static final int STATE_JOIN_GAME_MENU = 3;

    @SuppressWarnings("unused")
    private int menuState = STATE_LOGIN;

    private Console console;

    private Menu menu;

    @SuppressWarnings("unused")
    private GUI gui;

    @Override
    public void init(GUI incGui) {
        this.gui = incGui;
        initState(STATE_LOGIN);
        console.setVisible(false);
        incGui.initState(this);
    }

    private void initState(int state) {
        menuState = state;
        console = new Console();
        switch (state) {
        case STATE_LOGIN:
            menu = new Menu();
            break;
        case STATE_MAIN_MENU:
            // menu.setVisible(false);
            break;
        case STATE_CREATE_GAME_MENU:

            break;
        case STATE_JOIN_GAME_MENU:

            break;
        default:
            break;
        }
    }

    @Override
    public void handleMessage(ClientMessage msg) {}

    @Override
    public void loop(long tickTime) {

    }

    @Override
    public void handleInput(Map<Integer, Integer> keyMap) {
        if (keyMap.get(Keyboard.KEY_RETURN) != null && keyMap.get(Keyboard.KEY_RETURN) == 1) {
            initState(STATE_MAIN_MENU);
        }
        console.handleInput(keyMap);
        menu.handleInput(keyMap);
    }

    @Override
    public List<BasicUIElement> getUI() {
        List<BasicUIElement> ui = new LinkedList<>();
        ui.add(console);
        ui.add(menu);
        return ui;
    }
}
