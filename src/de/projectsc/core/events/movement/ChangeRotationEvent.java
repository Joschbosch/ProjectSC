/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.movement;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;

public class ChangeRotationEvent extends Event {

    public static final String id = "RotateEvent";

    private boolean isRelative = false;

    private Vector3f newRotation;

    public ChangeRotationEvent(Vector3f newRotation, long entityId) {
        super(id, entityId);
        this.newRotation = newRotation;
    }

    public ChangeRotationEvent(float dx, float dy, float dz, long entityId) {
        super(id, entityId);
        this.newRotation = new Vector3f(dx, dy, dz);
        isRelative = true;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public Vector3f getNewRotation() {
        return newRotation;
    }

}
