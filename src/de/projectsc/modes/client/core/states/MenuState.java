/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.core.states;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.lwjgl.input.Keyboard;

import de.projectsc.core.data.KeyboardInputCommand;
import de.projectsc.core.data.MouseInputCommand;
import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.interfaces.InputCommandListener;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.messages.GameMessageConstants;
import de.projectsc.core.messages.MessageConstants;
import de.projectsc.modes.client.core.data.ClientGameContext;
import de.projectsc.modes.client.interfaces.ClientState;
import de.projectsc.modes.client.interfaces.GUI;
import de.projectsc.modes.client.messages.ClientMessage;
import de.projectsc.modes.client.ui.BasicUIElement;
import de.projectsc.modes.client.ui.elements.Console;
import de.projectsc.modes.client.ui.elements.Menu;

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
    public void init(GUI gui, BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager, ClientGameContext gameData) {
        super.init(gui, networkQueue, entityManager, eventManager, componentManager, gameData);
        initState(STATE_LOGIN);
        console.setVisible(false);
        gui.initState(this);
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
    public List<BasicUIElement> getUI() {
        List<BasicUIElement> ui = new LinkedList<>();
        ui.add(console);
        ui.add(menu);
        return ui;
    }

    @Override
    public Snapshot getSnapshot() {
        return null;
    }

}
