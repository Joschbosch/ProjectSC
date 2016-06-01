/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.animation;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Joint {

    private Joint parent = null;
    
    private List<Joint> children = new LinkedList<>();
    
    private Matrix4f localMatrix = new Matrix4f();

    private Matrix4f worldMatrix = new Matrix4f();

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
}
