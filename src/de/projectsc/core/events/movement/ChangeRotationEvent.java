/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.movement;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;

/**
 * Changes the rotation of an entity.
 * 
 * @author Josch Bosch
 */
public class ChangeRotationEvent extends Event {

    /**
     * ID.
     */
    public static final String ID = "RotateEvent";

    private boolean isRelative = false;

    private final Vector3f newRotation;

    public ChangeRotationEvent(Vector3f newRotation, long entityId) {
        super(ID, entityId);
        this.newRotation = newRotation;
    }

    public ChangeRotationEvent(float dx, float dy, float dz, long entityId) {
        super(ID, entityId);
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
