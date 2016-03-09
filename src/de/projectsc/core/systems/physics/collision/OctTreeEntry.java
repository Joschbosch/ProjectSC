/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.systems.physics.collision;

import de.projectsc.core.data.physics.BoundingVolume;
import de.projectsc.core.data.physics.Transform;

/**
 * Information of the entries in the octree.
 * 
 * @param <T> generic
 * @author Josch Bosch
 */
public class OctTreeEntry<T> {

    private T owner;

    private Transform transform;

    private BoundingVolume boundingVolume;

    public OctTreeEntry(T e, Transform t, BoundingVolume b) {
        this.setOwner(e);
        this.setTransform(t);
        this.setBoundingVolume(b);
    }

    public T getOwner() {
        return owner;
    }

    public void setOwner(T owner) {
        this.owner = owner;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public BoundingVolume getBoundingVolume() {
        return boundingVolume;
    }

    public void setBoundingVolume(BoundingVolume boundingVolume) {
        this.boundingVolume = boundingVolume;
    }

}
