/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.shaders;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.core.modes.client.gui.objects.Camera;

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

    public WireFrameShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        LOGGER.debug("WireFrame shader loaded.");
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
        super.loadMatrix(locationViewMatrix, camera.createViewMatrix());
    }

    /**
     * @param transformationMatrix to load into the shader
     */
    public void loadTransformationMatrix(Matrix4f transformationMatrix) {
        super.loadMatrix(locationTransformationMatrix, transformationMatrix);
    }
}