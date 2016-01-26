/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.physics;

import java.util.ArrayList;
import java.util.List;

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

    private List<Vector3f> tangents = new ArrayList<Vector3f>();

    private Vector3f averagedTangent = new Vector3f(0, 0, 0);

    public Vertex(int index, Vector3f position) {
        this.index = index;
        this.position = position;
        this.length = position.length();
    }

    /**
     * Clone vertex.
     * 
     * @param newIndex of vertex
     * @return new vertex
     */
    public Vertex duplicate(int newIndex) {
        Vertex vertex = new Vertex(newIndex, position);
        vertex.tangents = this.tangents;
        return vertex;
    }

    /**
     * Calc average.
     */
    public void averageTangents() {
        if (tangents.isEmpty()) {
            return;
        }
        for (Vector3f tangent : tangents) {
            Vector3f.add(averagedTangent, tangent, averagedTangent);
        }
        if (averagedTangent.length() > 0) {
            averagedTangent.normalise();
        }
    }

    /**
     * 
     * @param tangent to add
     */
    public void addTangent(Vector3f tangent) {
        tangents.add(tangent);
    }

    public Vector3f getAverageTangent() {
        return averagedTangent;
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
