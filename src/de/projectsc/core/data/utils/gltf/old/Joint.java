/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf.old;

import org.lwjgl.util.vector.Matrix4f;

public class Joint extends GLTFNode {

    private int id;

    private String name;

    private Matrix4f inverseBindMatrix = null;
    private Matrix4f bindPoseMatrix;
    public void updateMatrix(boolean updateChildren) {
        super.updateMatrix(updateChildren);
        if (bindPoseMatrix == null){
            bindPoseMatrix = new Matrix4f(localMatrix);
        }
        if (inverseBindMatrix == null){
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

}
