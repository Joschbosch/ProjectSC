/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.states;

import java.util.concurrent.BlockingQueue;

import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.manager.InputConsumeManager;
import de.projectsc.modes.client.core.data.ClientMessage;
import de.projectsc.modes.client.core.interfaces.ClientState;

public abstract class CommonClientState implements ClientState {

    protected BlockingQueue<ClientMessage> networkQueue;

    protected EntityManager entityManager;

    protected ComponentManager componentManager;

    protected EventManager eventManager;

    protected InputConsumeManager inputManager;

    @Override
    public void init(BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager, InputConsumeManager inputManager) {
        this.networkQueue = networkQueue;
        this.entityManager = entityManager;
        this.componentManager = componentManager;
        this.eventManager = eventManager;
        this.inputManager = inputManager;
    }

    public void sendMessage(ClientMessage e) {
        networkQueue.offer(e);
    }
}
