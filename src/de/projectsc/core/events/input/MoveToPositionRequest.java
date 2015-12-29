/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.input;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;

public class MoveToPositionRequest extends Event {

    public static final String ID = "MoveToPositionRequest";

    private final Vector3f target;

    public MoveToPositionRequest(Vector3f target) {
        super(ID);
        this.target = target;
    }

    public Vector3f getTarget() {
        return target;
    }
}
