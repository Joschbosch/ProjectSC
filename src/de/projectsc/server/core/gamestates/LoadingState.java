/*
 * Copyright (C) 2015
 */

package de.projectsc.server.core.gamestates;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.server.core.GameContext;
import de.projectsc.server.core.ServerPlayer;
import de.projectsc.server.core.messages.ServerMessage;

public class LoadingState extends GameState {

    private static final Log LOGGER = LogFactory.getLog(LoadingState.class);

    private GameContext context;

    @Override
    public void call(GameContext gameContext) throws Exception {
        this.context = gameContext;
        LOGGER.debug("Entered game state " + gameContext.getState());

    }

    @Override
    public void loop() {
        LOGGER.debug("Looping game state " + context.getState());
    }

    @Override
    public void handleMessage(ServerPlayer player, ServerMessage msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleMessages(ServerPlayer player, List<ServerMessage> msg) {
        // TODO Auto-generated method stub

    }
}
