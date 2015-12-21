/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.physics;

import org.lwjgl.util.vector.Vector3f;

/**
 * Vertex to be loaded by objloader.
 * 
 * @author Josch Bosch
 */
public class Vertex {

    private static final int NO_INDEX = -1;

    private final Vector3f position;

    private int textureIndex = NO_INDEX;

    private int normalIndex = NO_INDEX;

    private Vertex duplicateVertex = null;

    private final int index;

    private final float length;

    public Vertex(int index, Vector3f position) {
        this.index = index;
        this.position = position;
        this.length = position.length();
    }

    public int getIndex() {
        return index;
    }

    public float getLength() {
        return length;
    }

    public boolean isSet() {
        return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
    }

    /**
     * @param textureIndexOther to be checked
     * @param normalIndexOther to be checked
     * @return true, if both indices are the ones stored in this vertex
     */
    public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
        return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public void setNormalIndex(int normalIndex) {
        this.normalIndex = normalIndex;
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public int getNormalIndex() {
        return normalIndex;
    }

    public Vertex getDuplicateVertex() {
        return duplicateVertex;
    }

    public void setDuplicateVertex(Vertex duplicateVertex) {
        this.duplicateVertex = duplicateVertex;
    }

}
