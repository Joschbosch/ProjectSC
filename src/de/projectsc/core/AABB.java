/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core;

import org.lwjgl.util.vector.Vector3f;

/**
 * An axis aligned bounding box for each entitiy.
 * 
 * @author Josch Bosch
 */
public class AABB {

    private Vector3f min;

    private Vector3f max;

    private Vector3f center;

    private Vector3f size;

    /**
     * Minimum and maximum value for all axis.
     * 
     * @param min vector
     * @param max vector
     */
    public AABB(Vector3f min, Vector3f max) {
        super();
        this.min = min;
        this.max = max;
        center = Vector3f.add(min, max, null);
        center.scale(1.0f / 2.0f);
        setSize(Vector3f.sub(max, min, null));
    }

    public Vector3f getMin() {
        return min;
    }

    public void setMin(Vector3f min) {
        this.min = min;
    }

    public Vector3f getMax() {
        return max;
    }

    public void setMax(Vector3f max) {
        this.max = max;
    }

    public Vector3f getSize() {
        return size;
    }

    public void setSize(Vector3f size) {
        this.size = size;
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }

}
