/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import de.javagl.jgltf.impl.Node;

public class GLTFNode {

    private Map<String, GLTFNode> children;

    private GLTFNode root;

    private GLTFNode parent;

    private Vector3f position;

    private Vector3f scale;

    private Quaternion rotation;

    protected Matrix4f localMatrix;

    protected Matrix4f worldMatrix;

    private Node node;

    public GLTFNode(Node node) {
        children = new HashMap<>();
        root = null;
        parent = null;
        localMatrix = new Matrix4f();
        worldMatrix = new Matrix4f();
        this.node = node;
    }

    public void setLocalMatrix(Matrix4f matrix) {
        localMatrix = matrix;
    }

    public Matrix4f getWorldMatrix() {
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

    public Node getNode() {
        return node;
    }

    public void addChild(GLTFNode child) {
        children.put(child.getNode().getName(), child);
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

    public Matrix4f getLocalMatrix() {
        return localMatrix;
    }

}
