/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf.old;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.lwjgl.util.vector.Matrix4f;

public class GLTFSkeleton {

    private String name;

    private Map<String, Joint> joints = new HashMap<>();

    private Matrix4f bindShapeMatrix;

    public void setName(String key) {
        this.name = key;
    }

    public void addJoint(Joint joint) {
        joints.put(joint.getName(), joint);
    }

    public void setBindShapeMatrix(Matrix4f bindShape) {
        this.bindShapeMatrix = bindShape;
    }

    public Collection<Joint> getJoints() {
        return joints.values();
    }
    
    public Matrix4f getBindShapeMatrix() {
        return bindShapeMatrix;
    }
}
