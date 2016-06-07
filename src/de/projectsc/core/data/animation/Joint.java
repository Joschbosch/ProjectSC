/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.animation;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Joint {

    private static final Log LOGGER = LogFactory.getLog(Joint.class);

    private int id;

    private String name;

    private Joint parent = null;

    private List<Joint> children = new LinkedList<>();

    private Matrix4f localMatrix = new Matrix4f();

    private Matrix4f worldMatrix = new Matrix4f();

    private Matrix4f inverseBindMatrix = null;

    private Matrix4f parentMatrix = null;

    public void updateMatrix(boolean updateChildren) {
        if (parentMatrix != null && parent != null) {
            LOGGER.error("Joint has parent matrix and parent node: " + name);
        }
        if (parentMatrix != null) {
            this.worldMatrix = Matrix4f.mul(parentMatrix, localMatrix, null);
        } else {
            this.worldMatrix = Matrix4f.mul(parent.getWorldMatrix(), localMatrix, null);
        }
        if (inverseBindMatrix == null) {
            inverseBindMatrix = new Matrix4f(worldMatrix);
            inverseBindMatrix.invert();
        }
        if (updateChildren) {
            for (Joint child : children) {
                child.updateMatrix(updateChildren);
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Matrix4f getInverseBindMatrix() {
        return inverseBindMatrix;
    }

    public void setInverseBindMatrix(Matrix4f inverseBindMatrix) {
        this.inverseBindMatrix = inverseBindMatrix;
    }

    public Vector3f getWorldPosition() {
        return new Vector3f(worldMatrix.m30, worldMatrix.m31, worldMatrix.m32);
    }

    public void setWorldMatrix(Matrix4f worldMatrix) {
        this.worldMatrix = worldMatrix;
    }

    public void addChild(Joint child) {
        this.children.add(child);
    }

    public List<Joint> getChildren() {
        return children;
    }

    public Joint getParent() {
        return parent;
    }

    public void setParent(Joint parent) {
        this.parent = parent;
    }

    public Matrix4f getLocalMatrix() {
        return localMatrix;
    }

    public void setLocalMatrix(Matrix4f localMatrix) {
        this.localMatrix = localMatrix;
    }

    public Matrix4f getWorldMatrix() {
        return worldMatrix;
    }

    public void setParentMatrix(Matrix4f parentsWorldMatrix) {
        this.parentMatrix = parentsWorldMatrix;
    }

    public Matrix4f getParentMatrix() {
        return parentMatrix;
    }
}
