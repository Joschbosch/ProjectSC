/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class GLTFJoint {

    private String id;

    private String name;

    private Vector3f position;

    private Vector3f scale;

    private Quaternion rotation;

    public String getId() {
        return id;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public Vector3f getPosition() {
        return position;
    }

    
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    
    public Vector3f getScale() {
        return scale;
    }

    
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    
    public Quaternion getRotation() {
        return rotation;
    }

    
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    
    public void setId(String id) {
        this.id = id;
    }

}
