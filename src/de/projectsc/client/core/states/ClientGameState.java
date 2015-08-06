/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core.states;

import java.util.List;

import au.com.ds.ef.call.ContextHandler;
import de.projectsc.client.core.ClientGameContext;
import de.projectsc.client.core.messages.ClientMessage;

/**
 * Parent class of all states for the game state fsm.
 * 
 * @author Josch Bosch
 */
public abstract class ClientGameState implements ContextHandler<ClientGameContext> {

    protected ClientGameContext context;

    /**
     * Handles an incoming message from the given player.
     * 
     * @param msg to handle
     */
    public abstract void handleMessage(ClientMessage msg);

    /**
     * Handles incoming messages from the given player.
     * 
     * @param msg to handle
     */
    public abstract void handleMessages(List<ClientMessage> msg);

    /**
     * Main loop for the state.
     * 
     * @param tickTime mostly static
     */
    public abstract void loop(long tickTime);

    /**
     * Read input from user.
     */
    public abstract void readInput();

    protected void sendMessageToServer(ClientMessage msg) {
        context.getCore().sendMessageToServer(msg);
    }

    /**
     * Notice gui change.
     */
    public void changeGUI() {
        context.getGUI().changeState((ClientStates) context.getState());
    }

    /**
     * Rendering.
     * 
     * @param elapsed time since last rendering.
     * @param lag of rendering
     */
    public abstract void render(long elapsed, long lag);

}
