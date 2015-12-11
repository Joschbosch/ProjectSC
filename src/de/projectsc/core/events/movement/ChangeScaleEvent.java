/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.movement;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;

public class ChangeScaleEvent extends Event {

    public static final String ID = "ChangeScaleEvent";

    private final Vector3f newScale;

    public ChangeScaleEvent(long entityID, Vector3f newScale) {
        super(ID, entityID);
        this.newScale = newScale;
    }

    public Vector3f getNewScale() {
        return newScale;
    }

}
