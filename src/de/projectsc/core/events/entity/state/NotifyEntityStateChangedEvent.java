/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.state;

import de.projectsc.core.data.EntityEvent;

/**
 * Event if a new state was set.
 * 
 * @author Josch Bosch
 */
public class NotifyEntityStateChangedEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = NotifyEntityStateChangedEvent.class.getName();

    public NotifyEntityStateChangedEvent(String entityId) {
        super(ID, entityId);
    }

}
