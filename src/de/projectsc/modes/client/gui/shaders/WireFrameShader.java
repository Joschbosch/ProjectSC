/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.shaders;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.modes.client.gui.objects.Camera;

/**
 *
 * Shader for wireframe objects.
 * 
 * @author Josch Bosch
 */
public class WireFrameShader extends Shader {

    private static final Log LOGGER = LogFactory.getLog(WireFrameShader.class);

    private static final String VERTEX_FILE = "wireFrameShader.vert";

    private static final String FRAGMENT_FILE = "wireFrameShader.frag";

    private int locationProjectionMatrix;

    private int locationViewMatrix;

    private int locationTransformationMatrix;

    private int locationColor;

    public WireFrameShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        LOGGER.info("WireFrame shader loaded.");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationViewMatrix = super.getUniformLocation("viewMatrix");
        locationColor = super.getUniformLocation("color");
    }

    /**
     * Loads the projection matrix.
     *
     * @param matrix to store the projection matrix.
     */
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(locationProjectionMatrix, matrix);
    }

    /**
     * Loads the current view matrix.
     *
     * @param camera for information
     */
    public void loadViewMatrix(Camera camera) {
        super.loadMatrix(locationViewMatrix, camera.getViewMatrix());
    }

    /**
     * @param transformationMatrix to load into the shader
     */
    public void loadTransformationMatrix(Matrix4f transformationMatrix) {
        super.loadMatrix(locationTransformationMatrix, transformationMatrix);
    }

    /**
     * @param color to load.
     */
    public void loadColor(Vector3f color) {
        super.loadVector(locationColor, color);
    }
}
