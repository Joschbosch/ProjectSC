/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data;

public class Event {

    private String eventId;

    private long entityId;

    public Event(String eventId, long entityID) {
        this.eventId = eventId;
        this.entityId = entityID;
    }

    public String getEventID() {
        return eventId;
    }

    public long getEntityId() {
        return entityId;
    }
}
