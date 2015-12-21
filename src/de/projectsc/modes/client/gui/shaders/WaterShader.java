/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.shaders;

import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.modes.client.gui.objects.Camera;

/**
 * A shader just for the water effects.
 * 
 * @author Josch Bosch
 */
public class WaterShader extends Shader {

    private static final String VERTEX_FILE = "waterShader.vert";

    private static final String FRAGMENT_FILE = "waterShader.frag";

    private int locationModelMatrix;

    private int locationViewMatrix;

    private int locationProjectionMatrix;

    private int locationRefractionTexture;

    private int locationReflectionTexture;

    private int locationdudvTexture;

    private int locationMoveFactor;

    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = getUniformLocation("projectionMatrix");
        locationViewMatrix = getUniformLocation("viewMatrix");
        locationModelMatrix = getUniformLocation("modelMatrix");
        locationRefractionTexture = getUniformLocation("refractionTexture");
        locationReflectionTexture = getUniformLocation("reflectionTexture");
        locationdudvTexture = getUniformLocation("dudvTexture");
        locationMoveFactor = getUniformLocation("moveFactor");
    }

    /**
     * Load up move factor of waves.
     * 
     * @param factor to upload
     */
    public void loadMoveFactor(float factor) {
        loadFloat(locationMoveFactor, factor);
    }

    /**
     * Load up projection matrix.
     * 
     * @param projection matrix to load.
     */
    public void loadProjectionMatrix(Matrix4f projection) {
        loadMatrix(locationProjectionMatrix, projection);
    }

    /**
     * Load up view matrix.
     * 
     * @param camera cam
     */

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = camera.createViewMatrix();
        loadMatrix(locationViewMatrix, viewMatrix);
    }

    /**
     * Load up model matrix.
     * 
     * @param modelMatrix to upload.
     */
    public void loadModelMatrix(Matrix4f modelMatrix) {
        loadMatrix(locationModelMatrix, modelMatrix);
    }

    /**
     * load up texture positions.
     */
    public void loadTextures() {
        loadInt(locationReflectionTexture, 0);
        loadInt(locationRefractionTexture, 1);
        loadInt(locationdudvTexture, 2);
    }

}
