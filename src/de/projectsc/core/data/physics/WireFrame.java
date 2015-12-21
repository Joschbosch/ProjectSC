/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data.physics;

import org.lwjgl.util.vector.Vector3f;

/**
 * Wireframe representation of debugging things.
 * 
 * @author Josch Bosch
 */
public class WireFrame {

    /**
     * Wireframe type.
     */
    public static final String SPHERE = "Sphere";

    /**
     * Wireframe type.
     */
    public static final String CUBE = "Cube";

    /**
     * Wireframe type.
     */
    public static final String MESH = "Mesh";

    private String modelType = "";

    private final Vector3f position;

    private final Vector3f rotation;

    private final Vector3f scale;

    private final float lineWidth = 2;

    private Vector3f color = new Vector3f(0, 0, 1);

    public WireFrame(String type, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.modelType = type;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public String getModelType() {
        return modelType;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }
}
