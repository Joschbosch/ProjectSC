/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entities;

import de.projectsc.core.data.EntityEvent;

public class DeletedEntityEvent extends EntityEvent {

    private static final String NAME = "DeletedEntityEvent";

    public DeletedEntityEvent(String entityID) {
        super(NAME, entityID);
    }

}
