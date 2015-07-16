/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.ds.ef.EasyFlow;
import au.com.ds.ef.FlowBuilder;
import au.com.ds.ef.call.ContextHandler;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.client.core.states.ClientEvents;
import de.projectsc.client.core.states.ClientGameLobbyState;
import de.projectsc.client.core.states.ClientGameRunningState;
import de.projectsc.client.core.states.ClientGameState;
import de.projectsc.client.core.states.ClientLoadingState;
import de.projectsc.client.core.states.ClientLobbyState;
import de.projectsc.client.core.states.ClientStates;
import de.projectsc.server.core.game.GameContext;
import de.projectsc.server.core.game.states.GameRunningState;
import de.projectsc.server.core.game.states.States;

/**
 * Core class for the client.
 * 
 * @author Josch Bosch
 */
public class ClientCore implements Runnable {

    private static final int TICK_TIME = 50;

    private static final int SLEEP_TIME = 10;

    private static final String ERROR_IN_CORE = "Error in Core: ";

    private static final Log LOGGER = LogFactory.getLog(ClientCore.class);

    private static final Object LOCK_OBJECT = new Object();

    private BlockingQueue<ClientMessage> networkSendQueue;

    private BlockingQueue<ClientMessage> networkReceiveQueue;

    private ClientGameState currenState;

    private EasyFlow<ClientGameContext> flow;

    private ClientGameContext gameContext;

    private boolean clientRunning;

    public ClientCore() {
        networkSendQueue = new LinkedBlockingQueue<>();
        networkReceiveQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        LOGGER.debug("Starting core ... ");
        createAndBindFlow();
        clientRunning = true;
        gameContext = new ClientGameContext(this);
        flow.start(gameContext);

        long previous = System.currentTimeMillis();
        long lag = 0;
        LOGGER.debug(String.format("Client started"));
        while (clientRunning) {
            long current = System.currentTimeMillis();
            long elapsed = current - previous;
            previous = current;
            lag += elapsed;
            // readMessages();
            while (lag >= GameRunningState.GAME_TICK_TIME) {
                synchronized (LOCK_OBJECT) {
                    if (currenState != null) {
                        currenState.loop();
                    }
                }
                lag -= GameRunningState.GAME_TICK_TIME;
            }
            long timeNeeded = System.currentTimeMillis() - current;
            long sleepTime = Math.max((GameRunningState.GAME_TICK_TIME - timeNeeded), 0L);
            // LOGGER.debug(
            // String.format("Game %d needed %d ms for current tick, will sleep : %d",
            // gameContext.getGameID(), timeNeeded, sleepTime));
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOGGER.debug(e);
            }

            // render();
        }
        LOGGER.debug(String.format("Client terminated"));

    }

    private void createAndBindFlow() {
        flow = FlowBuilder.from(ClientStates.LOBBY)
            .transit(FlowBuilder.on(ClientEvents.ENTER_OR_CREATE_GAME).to(ClientStates.GAME_LOBBY)
                .transit(FlowBuilder.on(ClientEvents.START_GAME_COMMAND).to(ClientStates.LOADING)
                    .transit(FlowBuilder.on(ClientEvents.FINISHED_LOADING).to(ClientStates.RUNNING)
                        .transit(FlowBuilder.on(ClientEvents.GAME_ENDS).finish(ClientStates.FINISHED)))));

        flow.whenEnter(ClientStates.LOBBY, new ClientLobbyState());
        flow.whenEnter(ClientStates.GAME_LOBBY, new ClientGameLobbyState());

        flow.whenEnter(ClientStates.LOADING, new ClientLoadingState());
        flow.whenEnter(ClientStates.RUNNING, new ClientGameRunningState());
        flow.whenEnter(ClientStates.PAUSED, new ContextHandler<GameContext>() {

            @Override
            public void call(GameContext context) throws Exception {
                LOGGER.debug("Entered game state " + context.getState());
            }
        });
        flow.whenEnter(States.FINISHED, new ContextHandler<GameContext>() {

            @Override
            public void call(GameContext context) throws Exception {
                LOGGER.debug("Entered game state " + context.getState());
            }
        });
    }

    /**
     * Change current gamestate.
     * 
     * @param newState to change to
     */
    public void changeState(ClientGameState newState) {
        synchronized (LOCK_OBJECT) {
            currenState = newState;
            LOGGER.debug("Set client state to " + gameContext.getState());
        }
    }

    public BlockingQueue<ClientMessage> getNetworkSendQueue() {
        return networkSendQueue;
    }

    public void setNetworkSendQueue(BlockingQueue<ClientMessage> networkIncomingQueue) {
        this.networkSendQueue = networkIncomingQueue;
    }

    public BlockingQueue<ClientMessage> getNetworkReceiveQueue() {
        return networkReceiveQueue;
    }

    public void setNetworkReceiveQueue(BlockingQueue<ClientMessage> networkOutgoingQueue) {
        this.networkReceiveQueue = networkOutgoingQueue;
    }

}
