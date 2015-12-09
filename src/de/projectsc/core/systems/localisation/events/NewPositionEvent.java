/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.systems.localisation.events;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;

public class NewPositionEvent extends Event {

    public static final String ID = "NewPositionEvent";

    private Vector3f newPosition;

    private Vector3f newRotation;

    public NewPositionEvent(long entityId, Vector3f newPosition, Vector3f newRotation) {
        super(ID, entityId);
        this.newPosition = newPosition;
        this.newRotation = newRotation;

    }

    public Vector3f getNewPosition() {
        return newPosition;
    }

    public Vector3f getNewRotation() {
        return newRotation;
    }

}
