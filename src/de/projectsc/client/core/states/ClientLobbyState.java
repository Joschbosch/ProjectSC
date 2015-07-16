/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core.states;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.core.ClientGameContext;
import de.projectsc.client.core.messages.ClientMessage;

/**
 * State when the players are in a game lobby.
 * 
 * @author Josch Bosch
 */
public class ClientLobbyState extends ClientGameState {

    private static final Log LOGGER = LogFactory.getLog(ClientLobbyState.class);

    @Override
    public void call(ClientGameContext gameContext) throws Exception {
        this.context = gameContext;
        LOGGER.debug("Entered game state " + gameContext.getState());
        context.getCore().changeState(this);

    }

    @Override
    public void loop() {
        // LOGGER.debug("Looping game state " + context.getState());
    }

    @Override
    public void handleMessage(ClientMessage msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleMessages(List<ClientMessage> msg) {
        // TODO Auto-generated method stub

    }

}
