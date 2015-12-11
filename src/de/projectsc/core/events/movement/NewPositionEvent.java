/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.movement;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;

public class NewPositionEvent extends Event {

    public static final String ID = "NewPositionEvent";

    private final Vector3f newPosition;

    private final Vector3f newRotation;

    private final Vector3f scale;

    public NewPositionEvent(long entityId, Vector3f newPosition, Vector3f newRotation, Vector3f scale) {
        super(ID, entityId);
        this.newPosition = newPosition;
        this.newRotation = newRotation;
        this.scale = scale;

    }

    public Vector3f getNewPosition() {
        return newPosition;
    }

    public Vector3f getNewRotation() {
        return newRotation;
    }

    public Vector3f getScale() {
        return scale;
    }

}
