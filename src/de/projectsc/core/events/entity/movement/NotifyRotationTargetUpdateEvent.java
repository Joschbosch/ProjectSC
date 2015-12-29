/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.movement;

import de.projectsc.core.data.EntityEvent;

/**
 * Event if a new position was set.
 * 
 * @author Josch Bosch
 */
public class NotifyRotationTargetUpdateEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = NotifyRotationTargetUpdateEvent.class.getName();

    public NotifyRotationTargetUpdateEvent(String entityId) {
        super(ID, entityId);
    }

}
