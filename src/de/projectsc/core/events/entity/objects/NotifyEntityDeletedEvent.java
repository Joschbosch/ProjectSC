/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entity.objects;

import de.projectsc.core.data.EntityEvent;

/**
 * Entity was deleted.
 * 
 * @author Josch Bosch
 */
public class NotifyEntityDeletedEvent extends EntityEvent {

    private static final String NAME = NotifyEntityDeletedEvent.class.getName();

    public NotifyEntityDeletedEvent(String entityID) {
        super(NAME, entityID);
    }

}
