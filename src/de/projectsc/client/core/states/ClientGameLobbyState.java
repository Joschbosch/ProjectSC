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
import de.projectsc.client.core.elements.GameConfigurationView;
import de.projectsc.client.core.elements.Snapshot;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.core.data.messages.MessageConstants;

/**
 * State when the players are in a game lobby.
 * 
 * @author Josch Bosch
 */
public class ClientGameLobbyState extends ClientGameState {

    private static final Log LOGGER = LogFactory.getLog(ClientGameLobbyState.class);

    private GUI gui;

    private Snapshot snapshot;

    private GameConfigurationView config;

    private Chat chat;

    @Override
    public void call(ClientGameContext gameContext) throws Exception {
        this.context = gameContext;
        LOGGER.debug("Entered game state " + gameContext.getState());
        this.gui = context.getGUI();
        chat = new Chat();
        config = new GameConfigurationView(gameContext.getGameConfiguration());
        config.getGameConfiguration();
        context.getCore().changeState(this);
    }

    @Override
    public void handleMessage(ClientMessage msg) {
        if (msg.getMessage().equals(MessageConstants.PLAYER_JOINED_GAME)) {
            chat.addLine("Player joined game: " + msg.getData()[1]);
        } else if (msg.getMessage().equals(MessageConstants.CLIENT_LEFT_LOBBY)) {
            chat.addLine("Player left game: " + msg.getData()[1]);
        } else if (msg.getMessage().equals(MessageConstants.CHAT_MESSAGE)) {
            chat.addLine((String) msg.getData()[0]);
        }
    }

    @Override
    public void handleMessages(List<ClientMessage> msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loop(long tickTime) {
        createSnapshot();

    }

    private void createSnapshot() {
        snapshot = new Snapshot();
        snapshot.addUIElement(chat);
    }

    @Override
    public void readInput() {
        Queue<ClientMessage> input = gui.readInput();
        while (!input.isEmpty()) {
            ClientMessage msg = input.poll();
            if (msg.getMessage().equals(MessageConstants.CONNECT)) {
                sendMessageToServer(msg);
            }
        }
    }

    @Override
    public void render(long elapsed, long lag) {
        if (snapshot != null) {
            gui.render((ClientStates) context.getState(), elapsed, snapshot);
        }
    }

}
