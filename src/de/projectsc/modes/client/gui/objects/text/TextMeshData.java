package de.projectsc.modes.client.gui.objects.text;

/**
 * Stores the vertex data for all the quads on which a text will be rendered.
 * 
 * @author Karl TM
 * 
 */
public class TextMeshData {

    private float[] vertexPositions;

    private float[] textureCoords;

    protected TextMeshData(float[] vertexPositions, float[] textureCoords) {
        this.vertexPositions = vertexPositions;
        this.textureCoords = textureCoords;
    }

    public float[] getVertexPositions() {
        return vertexPositions;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public int getVertexCount() {
        return vertexPositions.length / 2;
    }

}
