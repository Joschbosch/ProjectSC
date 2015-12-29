/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data;

/**
 * Basic class of all events in the program.
 * 
 * @author Josch Bosch
 */
public class EntityEvent extends Event {

    private final String entityId;

    public EntityEvent(String eventId, String entityID) {
        super(eventId);
        this.entityId = entityID;
    }

    public String getEntityId() {
        return entityId;
    }

}
