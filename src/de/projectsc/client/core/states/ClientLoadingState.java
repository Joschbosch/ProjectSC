/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core.states;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.core.ClientGameContext;
import de.projectsc.client.core.messages.ClientMessage;

/**
 * State when loading all data for a started game.
 * 
 * @author Josch Bosch
 */
public class ClientLoadingState extends ClientGameState {

    private static final Log LOGGER = LogFactory.getLog(ClientLoadingState.class);

    private ClientGameContext context;

    private Map<Long, Byte> playerLoadingProgress;

    @Override
    public void call(ClientGameContext gameContext) throws Exception {
        this.context = gameContext;
        LOGGER.debug("Entered game state " + gameContext.getState());
        playerLoadingProgress = new TreeMap<>();
        new Thread(new Runnable() {

            @Override
            public void run() {}
        }).start();
        context.getCore().changeState(this);

    }

    @Override
    public void handleMessage(ClientMessage msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleMessages(List<ClientMessage> msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loop() {
        // TODO Auto-generated method stub

    }
}
