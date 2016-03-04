/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entity.actions;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.EntityEvent;

/**
 * Actions that the player wants to move to the specified target.
 * 
 * @author Josch Bosch
 */
public class MoveEntityToAttackTargetEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = MoveEntityToAttackTargetEvent.class.getName();

    private final Vector3f target;

    public MoveEntityToAttackTargetEvent(String entityID, Vector3f target) {
        super(ID, entityID);
        this.target = target;
    }

    public Vector3f getTarget() {
        return target;
    }

}
