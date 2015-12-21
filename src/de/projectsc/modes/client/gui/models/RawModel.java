/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.models;

/**
 * Stores the models vao.
 * 
 * @author Josch Bosch
 */
public class RawModel {

    private final int vaoID;

    private final int vertexCount;

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
