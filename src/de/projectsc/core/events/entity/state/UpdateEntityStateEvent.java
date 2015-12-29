/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.state;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.entities.states.EntityState;

/**
 * Changes the state of an entity.
 * 
 * @author Josch Bosch
 */
public class UpdateEntityStateEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = UpdateEntityStateEvent.class.getName();

    private final EntityState entityState;

    public UpdateEntityStateEvent(String entityId, EntityState newState) {
        super(ID, entityId);
        entityState = newState;
    }

    public EntityState getEntityState() {
        return entityState;
    }

}
