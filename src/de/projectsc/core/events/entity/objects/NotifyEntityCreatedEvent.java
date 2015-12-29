/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.objects;

import de.projectsc.core.data.EntityEvent;

/**
 * Event if an entity was created.
 * 
 * @author Josch Bosch
 */
public class NotifyEntityCreatedEvent extends EntityEvent {

    private static final String NAME = NotifyEntityCreatedEvent.class.getName();

    public NotifyEntityCreatedEvent(String entityID) {
        super(NAME, entityID);
    }

}
