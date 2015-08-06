/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.shaders;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.client.gui.objects.Camera;

public class CollisionBoxShader extends Shader {

    private static final Log LOGGER = LogFactory.getLog(CollisionBoxShader.class);

    private static final String VERTEX_FILE = "collisionBoxShader.vert";

    private static final String FRAGMENT_FILE = "collisionBoxShader.frag";

    private int locationProjectionMatrix;

    private int locationViewMatrix;

    private int locationTransformationMatrix;

    public CollisionBoxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        LOGGER.debug("Collision Box shader loaded.");
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

    public void loadTransformationMatrix(Matrix4f transformationMatrix) {
        super.loadMatrix(locationTransformationMatrix, transformationMatrix);
    }
}
