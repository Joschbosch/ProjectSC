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

    /**
     * Wireframe type.
     */
    public static final String LINE = "Line";

    private String modelType = "";

    private final Vector3f position;

    private final Vector3f rotation;

    private final Vector3f scale;

    private final float lineWidth = 2;

    private Vector3f color = new Vector3f(0, 0, 1);

    private Vector3f positionEnd;

    public WireFrame(String type, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.modelType = type;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public WireFrame(String type, Vector3f positionLineStart, Vector3f positionLineEnd) {
        this.modelType = type;
        this.position = positionLineStart;
        this.positionEnd = positionLineEnd;
        this.scale = new Vector3f(1, 1, 1);
        this.rotation = new Vector3f();
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
