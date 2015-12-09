/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.data.events;

import de.projectsc.core.data.Event;

public class ChangeScaleEvent extends Event {

    public static final String ID = "ChangeScaleEvent";

    private final float newScale;

    public ChangeScaleEvent(long entityID, float newScale) {
        super(ID, entityID);
        this.newScale = newScale;
    }

    public float getNewScale() {
        return newScale;
    }

}
