/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core.states;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.core.ClientGameContext;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.core.Terrain;
import de.projectsc.core.entities.WorldEntity;
import de.projectsc.core.utils.OctTree;

/**
 * State when the game is started and running. This is the main state for all the game logic.
 * 
 * @author Josch Bosch
 */
public class ClientGameRunningState extends ClientGameState {

    /**
     * Constant.
     */
    public static final long GAME_TICK_TIME = 16;

    private static final Log LOGGER = LogFactory.getLog(ClientLoadingState.class);

    private OctTree<WorldEntity> collisionTree;

    private Terrain terrain;

    private Map<Integer, WorldEntity> staticEntities;

    private Map<Integer, WorldEntity> entities;

    private final long gameTick = 0;

    private long time = 0;

    @Override
    public void call(ClientGameContext context) throws Exception {
        LOGGER.debug("Entered game state " + context.getState());

        this.context = context;
        time = System.currentTimeMillis();
        context.getCore().changeState(this);

    }

    @Override
    public void loop() {}

    @Override
    public void handleMessage(ClientMessage msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleMessages(List<ClientMessage> msg) {
        // TODO Auto-generated method stub

    }
}
