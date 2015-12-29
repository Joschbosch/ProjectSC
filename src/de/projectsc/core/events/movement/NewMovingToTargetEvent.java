/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.movement;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.EntityEvent;

public class NewMovingToTargetEvent extends EntityEvent {

    public static final String ID = "NewMovingTargetEvent";

    private final Vector3f target;

    public NewMovingToTargetEvent(String entityID, Vector3f target) {
        super(ID, entityID);
        this.target = target;
    }

    public Vector3f getTarget() {
        return target;
    }

}
