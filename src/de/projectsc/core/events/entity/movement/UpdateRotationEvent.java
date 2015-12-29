/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.movement;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.EntityEvent;

/**
 * Changes the rotation of an entity.
 * 
 * @author Josch Bosch
 */
public class UpdateRotationEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = UpdateRotationEvent.class.getName();

    private boolean isRelative = false;

    private final Vector3f newRotation;

    public UpdateRotationEvent(String entityId, Vector3f newRotation) {
        super(ID, entityId);
        this.newRotation = newRotation;
    }

    public UpdateRotationEvent(float dx, float dy, float dz, String entityId) {
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
