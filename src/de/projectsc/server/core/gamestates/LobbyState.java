/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core.gamestates;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.server.core.GameContext;
import de.projectsc.server.core.ServerPlayer;
import de.projectsc.server.core.messages.GameMessageConstants;
import de.projectsc.server.core.messages.ServerMessage;

/**
 * State when the players are in a game lobby.
 * 
 * @author Josch Bosch
 */
public class LobbyState extends GameState {

    private static final Log LOGGER = LogFactory.getLog(LobbyState.class);

    @Override
    public void call(GameContext gameContext) throws Exception {
        this.context = gameContext;
        gameContext.getGame().changeState(this);
        LOGGER.debug("Entered game state " + gameContext.getState());

    }

    @Override
    public void loop() {
        // LOGGER.debug("Looping game state " + context.getState());
    }

    @Override
    public void handleMessage(ServerPlayer player, ServerMessage msg) {

        if (msg.getMessage().equals(GameMessageConstants.START_GAME_REQUEST)) {
            String reason = checkIfGameIsStartAble(player);
            if (reason.isEmpty()) {
                context.trigger(Events.START_GAME_COMMAND);
                sendMessageToAllPlayers(new ServerMessage(GameMessageConstants.START_GAME));
            } else {
                sendMessageToPlayer(player, new ServerMessage(GameMessageConstants.ERROR_STARTING_GAME, reason));
            }
        }
    }

    private String checkIfGameIsStartAble(ServerPlayer player) {
        String response = "";
        if (!context.getHost().getId().equals(player.getId())) {
            response = "Error: Player starting game ist not host";
        }
        return response;
    }

    @Override
    public void handleMessages(ServerPlayer player, List<ServerMessage> msg) {

    }

}
