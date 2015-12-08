/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui.data;

import org.lwjgl.util.vector.Vector3f;

public class RenderEntity {

    private Vector3f position;

    private Vector3f rotation;

    private float scale = 1.0f;

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
