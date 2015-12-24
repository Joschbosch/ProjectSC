/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data;

/**
 * Basic class of all events in the program.
 * 
 * @author Josch Bosch
 */
public class Event {

    private final String eventId;

    private final String entityId;

    public Event(String eventId, String entityID) {
        this.eventId = eventId;
        this.entityId = entityID;
    }

    public String getEventID() {
        return eventId;
    }

    public String getEntityId() {
        return entityId;
    }
}
