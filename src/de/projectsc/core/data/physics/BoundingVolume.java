/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.physics;

import org.lwjgl.util.vector.Vector3f;

public interface BoundingVolume {

    Vector3f getPositionOffset();

    Vector3f getScale();

    Vector3f getMinima();

    Vector3f getMaxima();

    BoundingVolumeType getType();

    BoundingVolume cloneVolume();
}
