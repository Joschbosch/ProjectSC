/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.physics;

import org.lwjgl.util.vector.Vector3f;

/**
 * Interface for all kinds of volumes to describe bounding boxes.
 * 
 * @author Josch Bosch
 */
public interface BoundingVolume {

    /**
     * Get offset of volume (from entity position).
     * 
     * @return position offset
     */
    Vector3f getPositionOffset();

    /**
     * Volume scale.
     * 
     * @return scale
     */
    Vector3f getScale();

    /**
     * @return minima vector
     */
    Vector3f getMinima();

    /**
     * 
     * @return maxima vector
     */
    Vector3f getMaxima();

    /**
     * @return type of the volume.
     */
    BoundingVolumeType getType();

    /**
     * Clone volume.
     * 
     * @return new volume
     */
    BoundingVolume cloneVolume();

    /**
     * 
     * @param newPosition new position
     */
    void setPositionOffset(Vector3f newPosition);

    /**
     * Set new scale.
     * 
     * @param scale to set
     */
    void setScale(Vector3f scale);
}
