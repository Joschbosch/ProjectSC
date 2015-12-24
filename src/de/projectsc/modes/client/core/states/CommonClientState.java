/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.states;

import java.util.concurrent.BlockingQueue;

import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.modes.client.core.data.ClientGameContext;
import de.projectsc.modes.client.interfaces.ClientState;
import de.projectsc.modes.client.interfaces.GUI;
import de.projectsc.modes.client.messages.ClientMessage;

public abstract class CommonClientState implements ClientState {

    protected BlockingQueue<ClientMessage> networkQueue;

    protected GUI gui;

    protected EntityManager entityManager;

    protected ComponentManager componentManager;

    protected EventManager eventManager;

    protected ClientGameContext gameData;

    @Override
    public void init(GUI gui, BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager, ClientGameContext gameData) {
        this.networkQueue = networkQueue;
        this.gui = gui;
        this.entityManager = entityManager;
        this.componentManager = componentManager;
        this.eventManager = eventManager;
        this.gameData = gameData;
    }

    public void sendMessage(ClientMessage e) {
        networkQueue.offer(e);
    }
}
