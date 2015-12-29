/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entity.movement;

import de.projectsc.core.data.EntityEvent;

/**
 * Notification that the velocity was updated.
 * 
 * @author Josch Bosch
 */
public class NotifyVelocityUpdateEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = NotifyVelocityUpdateEvent.class.getName();

    public NotifyVelocityUpdateEvent(String entityId) {
        super(ID, entityId);

    }

}
