/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.states;

import java.util.concurrent.BlockingQueue;

import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.modes.client.core.data.ClientMessage;
import de.projectsc.modes.client.core.interfaces.ClientState;
import de.projectsc.modes.client.core.manager.ClientSnapshotManger;
import de.projectsc.modes.client.core.ui.UIElement;

/**
 * Basic implementation for the client states.
 * 
 * @author Josch Bosch
 */
public abstract class CommonClientState extends UIElement implements ClientState {

    protected BlockingQueue<ClientMessage> networkQueue;

    protected EntityManager entityManager;

    protected ComponentManager componentManager;

    protected EventManager eventManager;

    protected Timer timer;

    protected ClientSnapshotManger snapshotManager;

    public CommonClientState(String id, int order) {
        super(id, order);
    }

    @Override
    public void init(BlockingQueue<ClientMessage> incNetworkQueue, EntityManager incEntityManager, EventManager incEventManager,
        ComponentManager incComponentManager, ClientSnapshotManger snapshotmanager, Timer incTimer) {
        this.networkQueue = incNetworkQueue;
        this.entityManager = incEntityManager;
        this.componentManager = incComponentManager;
        this.eventManager = incEventManager;
        this.snapshotManager = snapshotmanager;
        this.timer = incTimer;
    }

    /**
     * Send a network message.
     * 
     * @param e to send
     */
    public void sendMessage(ClientMessage e) {
        networkQueue.offer(e);
    }
}
