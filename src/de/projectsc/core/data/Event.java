/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.data;

public class Event {

    protected final String eventId;

    public Event(String id) {
        this.eventId = id;
    }

    public String getEventID() {
        return eventId;
    }
}
