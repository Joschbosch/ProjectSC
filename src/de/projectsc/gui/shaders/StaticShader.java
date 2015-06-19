/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.shaders;

import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.gui.Camera;
import de.projectsc.gui.render.Light;

/**
 * A shader program that does not much.
 * 
 * @author Josch Bosch
 */
public class StaticShader extends Shader {

    private static final String VERTEX_FILE = "tutShader.vert";

    private static final String FRAGMENT_FILE = "tutShader.frag";

    private int locationTransformationMatrix;

    private int locationProjectionMatrix;

    private int locationViewMatrix;

    private int locationLightPosition;

    private int locationLightColor;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normals");

    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationViewMatrix = super.getUniformLocation("viewMatrix");
        locationLightPosition = super.getUniformLocation("lightPosition");
        locationLightColor = super.getUniformLocation("lightColor");
    }

    /**
     * Loads the transformation matrix.
     * 
     * @param matrix to store the matrix in.
     */
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(locationTransformationMatrix, matrix);
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
     * Loads up a light to the shader.
     * 
     * @param light to load
     */
    public void loadLight(Light light) {
        super.loadVector(locationLightPosition, light.getPosition());
        super.loadVector(locationLightColor, light.getColor());
    }
}
