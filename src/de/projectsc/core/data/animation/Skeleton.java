/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.animation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

public class Skeleton {

    private String name;

    private Map<String, Joint> joints = new HashMap<>();

    private List<Joint> rootJoints = new LinkedList<>();

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

    public void addRootJoint(Joint joint) {
        rootJoints.add(joint);
    }

    public Joint getJoint(int jointId) {
        for (Joint j : joints.values()) {
            if (j.getId() == jointId) {
                return j;
            }
        }
        return null;
    }
}
