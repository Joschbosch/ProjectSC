/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.client.gui.models;

/**
 * Stores the models vao.
 * 
 * @author Josch Bosch
 */
public class RawModel {

    private final int vaoID;

    private int[] streamingVBOs;

    private final int vertexCount;

    private int vboCount;

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

    public int getBuffer(int index) {
        if (streamingVBOs != null) {
            return streamingVBOs[index];
        }
        return -1;
    }

    public void addStreamingBuffer(int vboID) {
        if (streamingVBOs == null) {
            streamingVBOs = new int[5];
            streamingVBOs[0] = vboID;
            vboCount = 1;
        } else {
            streamingVBOs[vboCount++] = vboID;
        }
    }

}
