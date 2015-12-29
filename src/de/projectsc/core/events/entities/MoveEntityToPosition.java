/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entities;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.EntityEvent;

public class MoveEntityToPosition extends EntityEvent {

    public static final String ID = "MoveToPositionRequest";

    private final Vector3f target;

    public MoveEntityToPosition(String id, Vector3f target) {
        super(ID, id);
        this.target = target;
    }

    public Vector3f getTarget() {
        return target;
    }
}
