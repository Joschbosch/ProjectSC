/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.movement;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.EntityEvent;

/**
 * Changes the scale of an entity.
 * 
 * @author Josch Bosch
 */
public class ChangeScaleEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = "ChangeScaleEvent";

    private final Vector3f newScale;

    public ChangeScaleEvent(String entityID, Vector3f newScale) {
        super(ID, entityID);
        this.newScale = newScale;
    }

    public Vector3f getNewScale() {
        return newScale;
    }

}
