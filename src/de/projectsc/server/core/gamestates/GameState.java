/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core.gamestates;

import java.util.List;

import au.com.ds.ef.call.ContextHandler;
import de.projectsc.server.core.GameContext;
import de.projectsc.server.core.ServerPlayer;
import de.projectsc.server.core.messages.ServerMessage;

/**
 * Parent class of all states for the game state fsm.
 * 
 * @author Josch Bosch
 */
public abstract class GameState implements ContextHandler<GameContext> {

    protected GameContext context;

    /**
     * Handles an incoming message from the given player.
     * 
     * @param player with the message
     * @param msg to handle
     */
    public abstract void handleMessage(ServerPlayer player, ServerMessage msg);

    /**
     * Handles incoming messages from the given player.
     * 
     * @param player with the messages
     * @param msg to handle
     */
    public abstract void handleMessages(ServerPlayer player, List<ServerMessage> msg);

    /**
     * Main loop for the state.
     */
    public abstract void loop();

    protected void sendMessageToAllPlayers(ServerMessage message) {
        for (ServerPlayer player : context.getPlayers().values()) {
            player.getClient().sendMessage(message);
        }
    }

    protected void sendMessageToPlayer(ServerPlayer player, ServerMessage serverMessage) {
        player.getClient().sendMessage(serverMessage);
    }

}