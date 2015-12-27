/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.game.states;

import java.util.concurrent.BlockingQueue;

import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.messages.GameMessageConstants;
import de.projectsc.core.messages.MessageConstants;
import de.projectsc.modes.client.core.data.ClientMessage;
import de.projectsc.modes.client.core.interfaces.ClientState;
import de.projectsc.modes.client.core.states.CommonClientState;
import de.projectsc.modes.client.game.ui.controls.Console;
import de.projectsc.modes.client.game.ui.controls.Menu;

/**
 * The menu is the state when no game is running. The player can change options, create or join games or do many more things.
 * 
 * @author Josch Bosch
 */
public class MenuState extends CommonClientState {

    public static final int STATE_LOGIN = 0;

    public static final int STATE_MAIN_MENU = 1;

    public static final int STATE_CREATE_GAME_MENU = 2;

    public static final int STATE_JOIN_GAME_MENU = 3;

    private static MenuState instance;

    private int menuState = STATE_LOGIN;

    private Console console;

    private Menu menu;

    public MenuState() {
        super("MenuState", 0);
    }

    @Override
    public void init(BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager) {
        super.init(networkQueue, entityManager, eventManager, componentManager);
        setInstance(this);
        initState(STATE_LOGIN);
    }

    private void initState(int state) {
        menuState = state;
        // console = new Console();
        switch (state) {
        case STATE_LOGIN:
            menu = new Menu();
            console = new Console();
            break;
        case STATE_MAIN_MENU:
            menu.setActive(false);
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
    public ClientState handleMessage(ClientMessage msg) {
        if (msg.getMessage().equals(MessageConstants.CLIENT_JOINED_LOBBY)) {
            initState(STATE_MAIN_MENU);
        } else if (msg.getMessage().equals(MessageConstants.NEW_GAME_CREATED)) {
            initState(STATE_CREATE_GAME_MENU);
        } else if (msg.getMessage().equals(GameMessageConstants.START_GAME)) {
            return new GameState();
        }
        return null;
    }

    @Override
    public void loop(long tickTime) {

    }

    @Override
    public String getId() {
        return "Menu";
    }

    public static MenuState getInstance() {
        return instance;
    }

    public static void setInstance(MenuState instance) {
        MenuState.instance = instance;
    }

    public int getState() {
        return menuState;
    }

}
