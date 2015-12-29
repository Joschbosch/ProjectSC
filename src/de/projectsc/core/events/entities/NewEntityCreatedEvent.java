/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entities;

import de.projectsc.core.data.EntityEvent;

/**
 * Event if an entity was created.
 * 
 * @author Josch Bosch
 */
public class NewEntityCreatedEvent extends EntityEvent {

    private static final String NAME = "NewEntityEvent";

    public NewEntityCreatedEvent(String entityID) {
        super(NAME, entityID);
    }

}
