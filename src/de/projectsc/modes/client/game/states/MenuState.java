/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.game.states;

import java.util.concurrent.BlockingQueue;

import org.lwjgl.input.Keyboard;

import de.projectsc.core.data.KeyboardInputCommand;
import de.projectsc.core.data.MouseInputCommand;
import de.projectsc.core.interfaces.InputCommandListener;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.manager.InputConsumeManager;
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
public class MenuState extends CommonClientState implements InputCommandListener {

    private static final int STATE_LOGIN = 0;

    private static final int STATE_MAIN_MENU = 1;

    private static final int STATE_CREATE_GAME_MENU = 2;

    private static final int STATE_JOIN_GAME_MENU = 3;

    private int menuState = STATE_LOGIN;

    private Console console;

    private Menu menu;

    @Override
    public void init(BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager, InputConsumeManager inputManager) {
        super.init(networkQueue, entityManager, eventManager, componentManager, inputManager);
        initState(STATE_LOGIN);
        // console.setVisible(false);
    }

    private void initState(int state) {
        menuState = state;
        // console = new Console();
        switch (state) {
        case STATE_LOGIN:
            menu = new Menu();
            console = new Console();
            inputManager.addListener(menu);
            inputManager.addListener(console);
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
    public void handleKeyboardCommand(KeyboardInputCommand command) {
        if (command.isKeyDown()) {
            if (command.getKey() == Keyboard.KEY_RETURN) {
                if (command.isKeyDown() && !command.isKeyRepeated()) {
                    if (menuState == STATE_LOGIN) {
                        sendMessage(new ClientMessage(MessageConstants.CONNECT));
                    }
                    if (menuState == STATE_MAIN_MENU) {
                        sendMessage(new ClientMessage(MessageConstants.CREATE_NEW_GAME_REQUEST));

                    }
                    if (menuState == STATE_CREATE_GAME_MENU) {
                        sendMessage(new ClientMessage(GameMessageConstants.START_GAME_REQUEST));
                    }
                }
                command.consumed();
            }
        }
    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {

    }

    @Override
    public InputConsumeLevel getInputConsumeLevel() {
        return InputConsumeLevel.SECOND;
    }

    @Override
    public String getId() {
        return "Menu";
    }

}
