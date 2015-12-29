/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.movement;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.EntityEvent;

/**
 * Changes the scale of an entity.
 * 
 * @author Josch Bosch
 */
public class UpdateScaleEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = UpdateScaleEvent.class.getName();

    private final Vector3f newScale;

    public UpdateScaleEvent(String entityID, Vector3f newScale) {
        super(ID, entityID);
        this.newScale = newScale;
    }

    public Vector3f getNewScale() {
        return newScale;
    }

}
