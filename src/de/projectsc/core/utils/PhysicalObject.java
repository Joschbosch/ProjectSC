/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.utils;

import org.lwjgl.util.vector.Vector3f;

/**
 * Interface for objects in the world which may be checked against collision.
 *
 * @author Josch Bosch
 */
public interface PhysicalObject {

    /**
     * @return is the object movable?
     */
    boolean isMovable();

    /**
     * 
     * @return {@link BoundingBox} of the Object
     */
    BoundingBox getBoundingBox();

    /**
     * @return current position of the object
     */
    Vector3f getPosition();

    /**
     * @return true, if the object moved in the current tick.
     */
    boolean hasMoved();
}
