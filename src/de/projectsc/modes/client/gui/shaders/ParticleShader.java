/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Shader for Particle effects.
 * 
 * @author Josch Bosch
 */
public class ParticleShader extends Shader {

    private static final String VERTEX_FILE = "particleShader.vert";

    private static final String FRAGMENT_FILE = "particleShader.frag";

    private int locationCameraRightWorldspace;

    private int locationCameraUpWorldspace;

    private int locationModelViewProjectionMatrix;

    private int locationTexture;

    private int locationNumberOfRows;

    public ParticleShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Loads the transformation matrix to the shader.
     * 
     * @param matrix transformation matrix
     * @param proMatrix projection matrix
     */
    public void loadPositionAttributes(Matrix4f matrix, Matrix4f proMatrix) {
        super.loadVector(locationCameraRightWorldspace, new Vector3f(matrix.m00, matrix.m10, matrix.m20));
        super.loadVector(locationCameraUpWorldspace, new Vector3f(matrix.m01, matrix.m11, matrix.m21));
        super.loadMatrix(locationModelViewProjectionMatrix, Matrix4f.mul(proMatrix, matrix, null));
    }

    /**
     * Load up texture for particles.
     * 
     * @param textureID to load
     */
    public void loadTexture(int textureID) {
        super.loadInt(locationTexture, textureID);
    }

    @Override
    protected void getAllUniformLocations() {
        locationCameraRightWorldspace = super.getUniformLocation("cameraRightWorldspace");
        locationCameraUpWorldspace = super.getUniformLocation("cameraUpWorldspace");
        locationModelViewProjectionMatrix = super.getUniformLocation("modelViewProjectionMatrix");
        locationTexture = super.getUniformLocation("myTextureSampler");
        locationNumberOfRows = super.getUniformLocation("numberOfRows");

    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "squareVertices;");
        super.bindAttribute(1, "xyzs");
        super.bindAttribute(2, "color");
        super.bindAttribute(3, "uvCoords");
    }

    /**
     * @param numberOfRows in the texture file
     */
    public void loaderNumberOfRows(float numberOfRows) {
        super.loadFloat(locationNumberOfRows, numberOfRows);
    }

}
