/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.input;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;

/**
 * After a click, it was resolved that it was a move to position event.
 * 
 * @author Josch Bosch
 */
public class MoveToPositionAction extends Event {

    /**
     * ID.
     */
    public static final String ID = MoveToPositionAction.class.getName();

    private final Vector3f target;

    public MoveToPositionAction(Vector3f target) {
        super(ID);
        this.target = target;
    }

    public Vector3f getTarget() {
        return target;
    }
}
