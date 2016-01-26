/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.data.physics;

/**
 * Loaded data from an obj file.
 * 
 * @author Josch Bosch
 */
public class ModelData {

    private final float[] vertices;

    private final float[] textureCoords;

    private final float[] normals;

    private float[] tangents;

    private final int[] indices;

    private final float furthestPoint;

    public ModelData(float[] vertices, float[] textureCoords, float[] tangents, float[] normals, int[] indices,
        float furthestPoint) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.furthestPoint = furthestPoint;
        this.tangents = tangents;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public int[] getIndices() {
        return indices;
    }

    public float getFurthestPoint() {
        return furthestPoint;
    }

    public float[] getTangents() {
        return tangents;
    }

}
