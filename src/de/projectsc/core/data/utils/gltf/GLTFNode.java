/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import java.util.LinkedList;
import java.util.List;

import de.javagl.jgltf.impl.Node;

public class GLTFNode extends Node {

    private List<GLTFNode> children;

    private GLTFNode root;

    private GLTFNode parent;

    public GLTFNode() {
        children = new LinkedList<>();
        root = null;
        parent = null;
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
        children.add(child);
        child.setParent(this);
        if (this.root != null){
            child.setRoot(root);
        }
    }

}
