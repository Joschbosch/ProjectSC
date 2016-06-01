/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf.old;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import de.javagl.jgltf.impl.Node;
import de.projectsc.core.utils.Maths;

public class GLTFNode extends Node {

    private Map<String, GLTFNode> children;

    private GLTFNode root;

    private GLTFNode parent;

    private Vector3f position;

    private Vector3f scale;

    private Quaternion rotation;

    protected Matrix4f localMatrix;
    
    protected Matrix4f worldMatrix;

    private boolean needsUpdate = true;

    public GLTFNode() {
        children = new HashMap<>();
        root = null;
        parent = null;
        localMatrix = new Matrix4f();
        worldMatrix = new Matrix4f();
    }

    public void applyMatrix(Matrix4f matrix) {
        Matrix4f.mul(localMatrix, matrix, localMatrix);
    }

    public void updateMatrix(boolean updateChildren) {
        if (needsUpdate) {
            localMatrix = Maths.createTransformationMatrix(rotation, position, scale);
        }
        if (parent != null){
            Matrix4f.mul(parent.getWorldMatrix(), localMatrix, worldMatrix);
        } else {
            worldMatrix = new Matrix4f(localMatrix);
        }
        if (updateChildren){
            for (GLTFNode child : children.values()){
                child.updateMatrix(updateChildren);
            }
        }
    }

    private Matrix4f getWorldMatrix() {
        return worldMatrix;
    }

    public GLTFNode getRoot() {
        return root;
    }

    public void setRoot(GLTFNode root) {
        this.root = root;
    }

    public GLTFNode getParent() {
        return parent;
    }

    public void setParent(GLTFNode parent) {
        this.parent = parent;
    }

    public void addChild(GLTFNode child) {
        children.put(child.getName(), child);
        child.setParent(this);
        if (this.root != null) {
            child.setRoot(root);
        }
    }

    public GLTFNode getChild(String name) {
        return children.get(name);
    }

    public Map<String, GLTFNode> getChildNodes() {
        return children;
    }

    public Vector3f getPositionVector() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getScaleVector() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Quaternion getRotationQuaternion() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

}
