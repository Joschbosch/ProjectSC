/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.movement;

import de.projectsc.core.data.EntityEvent;

/**
 * Event if a new target rotation was set.
 * 
 * @author Josch Bosch
 */
public class NotifyTransformUpdateEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = NotifyTransformUpdateEvent.class.getName();

    public NotifyTransformUpdateEvent(String entityId) {
        super(ID, entityId);
    }

}
