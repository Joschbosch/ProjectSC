/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.animation;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Joint {

    private int id;

    private String name;

    private Joint parent = null;

    private List<Joint> children = new LinkedList<>();

    private Matrix4f localMatrix = new Matrix4f();

    private Matrix4f worldMatrix = new Matrix4f();

    private Matrix4f inverseBindMatrix = null;

    private Matrix4f parentMatrix = null;

    public void updateMatrix(boolean updateChildren) {
        if (inverseBindMatrix == null) {
            inverseBindMatrix = new Matrix4f(worldMatrix);
            inverseBindMatrix.invert();
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
        return new Vector3f(worldMatrix.m03, worldMatrix.m13, worldMatrix.m23);
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
}
