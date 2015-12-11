/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.data;

import org.lwjgl.util.vector.Vector3f;

public class WireFrame {

    public static final String SPHERE = "Sphere";

    public static final String MESH = "Mesh";

    private String modelType = "";

    private Vector3f position;

    private Vector3f rotation;

    private Vector3f scale;

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
}
