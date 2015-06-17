/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.gui.render;

/**
 * Stores the models vao.
 * 
 * @author Josch Bosch
 */
public class RawModel {

    private int vaoID;

    private int vertexCount;

    public RawModel(int vaoID, int vertexCount) {
        super();
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }

    public void setVaoID(int vaoID) {
        this.vaoID = vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

}
