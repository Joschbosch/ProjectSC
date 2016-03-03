/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.state;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.entities.states.EntityStates;

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

    private final EntityStates entityState;

    public UpdateEntityStateEvent(String entityId, EntityStates newState) {
        super(ID, entityId);
        entityState = newState;
    }

    public EntityStates getEntityState() {
        return entityState;
    }

}
