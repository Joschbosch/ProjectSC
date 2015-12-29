/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entities;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.entities.states.EntityState;

/**
 * Changes the state of an entity.
 * 
 * @author Josch Bosch
 */
public class ChangeEntityStateEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = "ChangeEntityStateEvent";

    private final EntityState entityState;

    public ChangeEntityStateEvent(String entityId, EntityState newState) {
        super(ID, entityId);
        entityState = newState;
    }

    public EntityState getEntityState() {
        return entityState;
    }

}
