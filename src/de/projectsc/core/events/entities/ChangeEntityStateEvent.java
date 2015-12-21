/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entities;

import de.projectsc.core.data.Event;
import de.projectsc.core.entities.states.EntityState;

/**
 * Changes the state of an entity.
 * 
 * @author Josch Bosch
 */
public class ChangeEntityStateEvent extends Event {

    /**
     * ID.
     */
    public static final String ID = "ChangeEntityStateEvent";

    private final EntityState entityState;

    public ChangeEntityStateEvent(long entityId, EntityState newState) {
        super(ID, entityId);
        entityState = newState;
    }

    public EntityState getEntityState() {
        return entityState;
    }

}
