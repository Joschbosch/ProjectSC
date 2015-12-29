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

public abstract class CommonClientState extends UIElement implements ClientState {

    public CommonClientState(String id, int order) {
        super(id, order);
    }

    protected BlockingQueue<ClientMessage> networkQueue;

    protected EntityManager entityManager;

    protected ComponentManager componentManager;

    protected EventManager eventManager;

    protected Timer timer;

    protected ClientSnapshotManger snapshotManager;

    @Override
    public void init(BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager, ClientSnapshotManger snapshotmanager, Timer timer) {
        this.networkQueue = networkQueue;
        this.entityManager = entityManager;
        this.componentManager = componentManager;
        this.eventManager = eventManager;
        this.snapshotManager = snapshotmanager;
        this.timer = timer;
    }

    public void sendMessage(ClientMessage e) {
        networkQueue.offer(e);
    }
}
