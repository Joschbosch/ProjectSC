/*
 * Copyright (C) 2015
 */

package de.projectsc.server.core.gamestates;

import java.util.List;

import au.com.ds.ef.call.ContextHandler;
import de.projectsc.server.core.GameContext;
import de.projectsc.server.core.ServerPlayer;
import de.projectsc.server.core.messages.ServerMessage;

public abstract class GameState implements ContextHandler<GameContext> {

    protected GameContext context;

    public abstract void handleMessage(ServerPlayer player, ServerMessage msg);

    public abstract void handleMessages(ServerPlayer player, List<ServerMessage> msg);

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
