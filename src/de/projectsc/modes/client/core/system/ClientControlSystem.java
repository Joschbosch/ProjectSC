/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.system;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import de.projectsc.core.component.state.ControlableComponent;
import de.projectsc.core.data.Event;
import de.projectsc.core.events.entity.objects.NotifyEntityCreatedEvent;
import de.projectsc.core.events.entity.objects.NotifyEntityDeletedEvent;
import de.projectsc.core.events.input.MoveToPositionAction;
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

    private Set<String> controllingEntities;

    private BlockingQueue<ClientMessage> networkSendQueue;

    public ClientControlSystem(EntityManager entityManager, EventManager eventManager, BlockingQueue<ClientMessage> networkSendQueue) {
        super("ClientControlSystem", entityManager, eventManager);
        controllingEntities = new HashSet<>();
        this.networkSendQueue = networkSendQueue;
        eventManager.registerForEvent(MoveToPositionAction.class, this);
        eventManager.registerForEvent(NotifyEntityDeletedEvent.class, this);
        eventManager.registerForEvent(NotifyEntityCreatedEvent.class, this);
    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof MoveToPositionAction) {
            for (String entity : controllingEntities) {
                networkSendQueue.offer(new ClientMessage("moveToPosition", entity, ((MoveToPositionAction) e).getTarget()));
            }
        }
        if (e instanceof NotifyEntityDeletedEvent) {
            removeEntity(((NotifyEntityDeletedEvent) e).getEntityId());
        }
        if (e instanceof NotifyEntityCreatedEvent) {
            String entityId = ((NotifyEntityCreatedEvent) e).getEntityId();
            if (hasComponent(entityId, ControlableComponent.class)) {
                addEntityToControl(entityId);
            }
        }
    }

    /**
     * Add a control over an entity.
     * 
     * @param e to control
     */
    public void addEntityToControl(String e) {
        controllingEntities.add(e);
    }

    /**
     * Remove control from en entity.
     * 
     * @param e to remove
     */
    public void removeEntity(String e) {
        controllingEntities.remove(e);
    }

    /**
     * Check if the client controls the given entity.
     * 
     * @param e the entity
     * @return true, if he does
     */
    public boolean controlsEntity(String e) {
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
