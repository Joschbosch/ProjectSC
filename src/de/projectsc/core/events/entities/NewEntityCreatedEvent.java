/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entities;

import de.projectsc.core.data.Event;

/**
 * Event if an entity was created.
 * 
 * @author Josch Bosch
 */
public class NewEntityCreatedEvent extends Event {

    private static final String NAME = "NewEntityEvent";

    public NewEntityCreatedEvent(long entityID) {
        super(NAME, entityID);
    }

}
