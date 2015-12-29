/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.movement;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.EntityEvent;

/**
 * Changes the position of the entity.
 * 
 * @author Josch Bosch
 */
public class ChangePositionEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = "ChangePositionEvent";

    private boolean isRelative = false;

    private final Vector3f newPosition;

    public ChangePositionEvent(Vector3f newPosition, String entityId) {
        super(ID, entityId);
        this.newPosition = newPosition;
    }

    public ChangePositionEvent(float dx, float dy, float dz, String entityId) {
        super(ID, entityId);
        this.newPosition = new Vector3f(dx, dy, dz);
        isRelative = true;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public Vector3f getNewPosition() {
        return newPosition;
    }
}
