/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.physics.boundings;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.BoundingVolume;
import de.projectsc.core.data.physics.BoundingVolumeType;
/**
 * A sphere as bounding volume.
 * @author Josch Bosch
 */
public class Sphere implements BoundingVolume {

    private Vector3f position = new Vector3f();

    private float radius = 1.0f;

    @Override
    public Vector3f getPositionOffset() {
        return position;
    }

    @Override
    public Vector3f getScale() {
        return new Vector3f(radius, radius, radius);
    }

    @Override
    public Vector3f getMinima() {
        return Vector3f.sub(position, getScale(), null);
    }

    @Override
    public Vector3f getMaxima() {
        return Vector3f.add(position, getScale(), null);
    }

    @Override
    public BoundingVolumeType getType() {
        return BoundingVolumeType.SPHERE;
    }

    @Override
    public BoundingVolume cloneVolume() {
        Sphere sphere = new Sphere();
        sphere.setPosition(position);
        sphere.setRadius(radius);
        return sphere;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

}
