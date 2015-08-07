/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.core.states;

import java.util.List;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.core.ClientGameContext;
import de.projectsc.client.core.GUI;
import de.projectsc.client.core.elements.Chat;
import de.projectsc.client.core.elements.LoginScreen;
import de.projectsc.client.core.elements.Snapshot;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.core.game.GameConfiguration;

/**
 * State when the players are in a game lobby.
 * 
 * @author Josch Bosch
 */
public class ClientLobbyState extends ClientGameState {

    private static final Log LOGGER = LogFactory.getLog(ClientLobbyState.class);

    private byte loginState = 0;

    private GUI gui;

    private Snapshot snapshot;

    private Chat chat;

    private LoginScreen loginScreen;

    @Override
    public void call(ClientGameContext gameContext) throws Exception {
        this.context = gameContext;
        this.gui = context.getGUI();
        chat = new Chat();
        loginScreen = new LoginScreen();
        LOGGER.debug("Entered game state " + gameContext.getState());
        context.getCore().changeState(this);
    }

    @Override
    public void loop(long tickTime) {
        createSnapshot();
    }

    private void createSnapshot() {
        snapshot = new Snapshot();
        snapshot.addUIElement(chat);
        if (loginState != 2) {
            snapshot.addUIElement(loginScreen);
            if (loginState == 3) {
                loginState = 2;
            }
            if (loginState == 1) {
                loginState = 3;
            }
        }
    }

    @Override
    public void handleMessage(ClientMessage msg) {
        if (msg.getMessage().equals(MessageConstants.SERVER_WELCOME)) {
            loginScreen.setLoginMessage("Connected to Server, loggin in...");
            sendMessageToServer(new ClientMessage(MessageConstants.CLIENT_LOGIN_REQUEST, "josch", "josch"));
            loginState = 0;
        } else if (msg.getMessage().equals(MessageConstants.LOGIN_FAILED)) {
            loginScreen.setLoginMessage("Failed to login: " + msg.getData()[0]);
            loginState = -1;
        } else if (msg.getMessage().equals(MessageConstants.LOGIN_SUCCESSFUL)) {
            loginScreen.setLoginMessage("Login successful");
            loginState = 1;
        } else if (msg.getMessage().equals(MessageConstants.CHAT_MESSAGE)) {
            chat.addLine((String) msg.getData()[0]);
        } else if (msg.getMessage().equals(MessageConstants.NEW_GAME_CREATED)) {
            context.setGameConfiguration((GameConfiguration) msg.getData()[0]);
            context.trigger(ClientEvents.ENTER_OR_CREATE_GAME);
        } else {
            LOGGER.error(String.format("Current state %s could not handle message: %s", context.getState(), msg));
        }
    }

    @Override
    public void handleMessages(List<ClientMessage> msgs) {
        for (ClientMessage msg : msgs) {
            handleMessage(msg);
        }
    }

    @Override
    public void readInput() {
        Queue<ClientMessage> input = context.getGUI().readInput();
        while (!input.isEmpty()) {
            ClientMessage msg = input.poll();
            sendMessageToServer(msg);
        }

    }

    @Override
    public void render(long elapsed, long lag) {
        if (snapshot != null) {
            gui.render((ClientStates) context.getState(), elapsed, snapshot);
        }
    }

}
