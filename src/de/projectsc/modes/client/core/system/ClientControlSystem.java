/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.system;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import de.projectsc.core.data.Event;
import de.projectsc.core.events.input.MoveToPositionAction;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;
import de.projectsc.modes.client.core.data.ClientMessage;

/**
 * System for controlling entities by the player.
 * 
 * @author Josch Bosch
 */
public class ClientControlSystem extends DefaultSystem {

    private Set<Entity> controllingEntities;

    private BlockingQueue<ClientMessage> networkSendQueue;

    public ClientControlSystem(EntityManager entityManager, EventManager eventManager, BlockingQueue<ClientMessage> networkSendQueue) {
        super("ClientControlSystem", entityManager, eventManager);
        controllingEntities = new HashSet<>();
        this.networkSendQueue = networkSendQueue;
        eventManager.registerForEvent(MoveToPositionAction.class, this);
    }

    @Override
    public void processEvent(Event e) {
        controllingEntities.add(entityManager.getEntity(entityManager.getAllEntites().iterator().next()));
        if (e instanceof MoveToPositionAction) {
            for (Entity entity : controllingEntities) {
                networkSendQueue.offer(new ClientMessage("moveToPosition", entity.getID(), ((MoveToPositionAction) e).getTarget()));
            }
        }
    }

    /**
     * Add a control over an entity.
     * 
     * @param e to control
     */
    public void addEntityToControl(Entity e) {
        controllingEntities.add(e);
    }

    /**
     * Remove control from en entity.
     * 
     * @param e to remove
     */
    public void removeEntity(Entity e) {
        controllingEntities.remove(e);
    }

    /**
     * Check if the client controls the given entity.
     * 
     * @param e the entity
     * @return true, if he does
     */
    public boolean controlsEntity(Entity e) {
        return controllingEntities.contains(e);
    }

    /**
     * Reset all controls.
     */
    public void resetControl() {
        controllingEntities.clear();
    }

    @Override
    public void update(long tick) {

    }

}
