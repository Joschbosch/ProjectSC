/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.system;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import de.projectsc.core.data.Event;
import de.projectsc.core.events.input.MoveToPositionRequest;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;
import de.projectsc.modes.client.core.data.ClientMessage;

public class ClientControlSystem extends DefaultSystem {

    private Set<Entity> controllingEntities;

    private BlockingQueue<ClientMessage> networkSendQueue;

    public ClientControlSystem(EntityManager entityManager, EventManager eventManager, BlockingQueue<ClientMessage> networkSendQueue) {
        super("ClientControlSystem", entityManager, eventManager);
        controllingEntities = new HashSet<>();
        this.networkSendQueue = networkSendQueue;
        eventManager.registerForEvent(MoveToPositionRequest.class, this);
    }

    @Override
    public void processEvent(Event e) {
        controllingEntities.add(entityManager.getEntity(entityManager.getAllEntites().iterator().next()));

        if (e instanceof MoveToPositionRequest) {
            for (Entity entity : controllingEntities) {
                // fireEvent(new MoveEntityToPosition(entity.getID(), ((MoveToPositionRequest) e).getTarget()));
                networkSendQueue.offer(new ClientMessage("moveToPosition", entity.getID(), ((MoveToPositionRequest) e).getTarget()));
            }
        }
    }

    public void addEntityToControl(Entity e) {
        controllingEntities.add(e);
    }

    public void removeEntity(Entity e) {
        controllingEntities.remove(e);
    }

    public boolean controlsEntity(Entity e) {
        return controllingEntities.contains(e);
    }

    public void resetControl() {
        controllingEntities.clear();
    }

    @Override
    public void update(long tick) {

    }

}
