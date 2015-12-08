/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events;

import de.projectsc.core.data.Event;
import de.projectsc.core.entities.EntityState;

public class ChangeEntityStateEvent extends Event {

    public static final String ID = "ChangeEntityStateEvent";

    private EntityState entityState;

    public ChangeEntityStateEvent(long entityId, EntityState newState) {
        super(ID, entityId);
        entityState = newState;
    }

    public EntityState getEntityState() {
        return entityState;
    }

}
