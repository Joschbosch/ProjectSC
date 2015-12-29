/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.state;

import de.projectsc.core.data.EntityEvent;

/**
 * Event if a new selection was set.
 * 
 * @author Josch Bosch
 */
public class NotifyEntitySelectionChangedEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = NotifyEntitySelectionChangedEvent.class.getName();

    public NotifyEntitySelectionChangedEvent(String entityId) {
        super(ID, entityId);
    }

}
