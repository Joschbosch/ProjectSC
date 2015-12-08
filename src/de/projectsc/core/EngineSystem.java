/*
 * Copyright (C) 2015 
 */

package de.projectsc.core;

import de.projectsc.core.data.Event;

public abstract class EngineSystem {

    private static long idCount = 0;

    protected final long uID;

    protected final String name;

    public EngineSystem(String name) {
        this.uID = idCount++;
        this.name = name;
    }

    public abstract void processEvent(Event e);

    public abstract void update();

    public String getName() {
        return name;
    }

    public long getuID() {
        return uID;
    }
}
