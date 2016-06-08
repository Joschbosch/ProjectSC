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

    private boolean texture;

    private boolean tangents;

    private boolean normals;

    public RawModel(int vaoID, int vertexCount) {
        super();
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.texture = true;
        this.tangents = false;
        this.normals = false;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setTangents(boolean value) {
        this.tangents = value;
    }

    public boolean hasTangents() {
        return tangents;
    }

    public void setTexture(boolean value) {
        this.texture = value;
    }

    public boolean hasTexture() {
        return texture;
    }

    public void setNormals(boolean value) {
        this.normals = value;
    }

    public boolean hasNormals() {
        return normals;
    }
}
