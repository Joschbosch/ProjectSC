/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entities;

import de.projectsc.core.data.Event;

public class DeletedEntityEvent extends Event {

    private static final String NAME = "DeletedEntityEvent";

    public DeletedEntityEvent(long entityID) {
        super(NAME, entityID);
    }

}
