/*
 * Copyright (C) 2015 
 */

package de.projectsc.gui.render;


public class RawModel {

    private int vaoID;

    public RawModel(int vaoID, int vertexCount) {
        super();
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    private int vertexCount;

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
