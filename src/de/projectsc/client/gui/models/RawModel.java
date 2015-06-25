/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.client.gui.models;

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

    public int getVertexCount() {
        return vertexCount;
    }

}
