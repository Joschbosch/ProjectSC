/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events;

import de.projectsc.core.data.Event;

public class NewEntityCreatedEvent extends Event {

    private static final String NAME = "NewEntityEvent";

    public NewEntityCreatedEvent(long entityID) {
        super(NAME, entityID);
    }

}
