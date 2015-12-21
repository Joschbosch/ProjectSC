/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Shader for all UI elements.
 * 
 * @author Josch Bosch
 */
public class BillboardShader extends Shader {

    private static final String VERTEX_FILE = "billboardShader.vert";

    private static final String FRAGMENT_FILE = "billboardShader.frag";

    private int locationCameraRightWorldspace;

    private int locationCameraUpWorldspace;

    private int locationModelViewProjectionMatrix;

    private int locationBillboardPos;

    private int locationBillboardSize;

    public BillboardShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Loads the transformation matrix to the shader.
     * 
     * @param matrix transformation matrix
     * @param proMatrix projection matrix.
     */
    public void loadPositionAttributes(Matrix4f matrix, Matrix4f proMatrix) {
        super.loadVector(locationCameraRightWorldspace, new Vector3f(matrix.m00, matrix.m10, matrix.m20));
        super.loadVector(locationCameraUpWorldspace, new Vector3f(matrix.m01, matrix.m11, matrix.m21));
        super.loadMatrix(locationModelViewProjectionMatrix, Matrix4f.mul(proMatrix, matrix, null));
    }

    /**
     * @param position to load
     * @param size to load
     */
    public void loadBillboardAttributes(Vector3f position, Vector2f size) {
        super.loadVector(locationBillboardPos, position);
        super.loadVector(locationBillboardSize, size);
    }

    @Override
    protected void getAllUniformLocations() {
        locationCameraRightWorldspace = super.getUniformLocation("cameraRightWorldspace");
        locationCameraUpWorldspace = super.getUniformLocation("cameraUpWorldspace");
        locationModelViewProjectionMatrix = super.getUniformLocation("modelViewProjectionMatrix");
        locationBillboardPos = super.getUniformLocation("billboardPos");
        locationBillboardSize = super.getUniformLocation("billboardSize");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
