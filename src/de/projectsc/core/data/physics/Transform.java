/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data.physics;

import org.lwjgl.util.vector.Vector3f;

/**
 * The current location, rotation and scale of an entity.
 * 
 * @author Josch Bosch
 */
public class Transform {

    private Vector3f position = new Vector3f(0, 0, 0);

    private Vector3f rotation = new Vector3f(0, 0, 0);

    private Vector3f scale = new Vector3f(1, 1, 1);

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }
}
