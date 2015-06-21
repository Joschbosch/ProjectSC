/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.shaders;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.gui.Camera;
import de.projectsc.gui.render.Light;

/**
 * A shader program that does not much.
 * 
 * @author Josch Bosch
 */
public class EntityShader extends Shader {

    private static final Log LOGGER = LogFactory.getLog(EntityShader.class);

    private static final String VERTEX_FILE = "entityShader.vert";

    private static final String FRAGMENT_FILE = "entityShader.frag";

    private int locationTransformationMatrix;

    private int locationProjectionMatrix;

    private int locationViewMatrix;

    private int locationLightPosition;

    private int locationLightColor;

    private int locationShineDamper;

    private int locationUseFakeLighting;

    private int locationReflectivity;

    private int locationSkyColor;

    public EntityShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        LOGGER.debug("Static shader loaded.");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");

    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationViewMatrix = super.getUniformLocation("viewMatrix");
        locationLightPosition = super.getUniformLocation("lightPosition");
        locationLightColor = super.getUniformLocation("lightColor");
        locationShineDamper = super.getUniformLocation("shineDamper");
        locationReflectivity = super.getUniformLocation("reflectivity");
        locationUseFakeLighting = super.getUniformLocation("useFakeLighting");
        locationSkyColor = super.getUniformLocation("skyColor");
    }

    public void loadSkyColor(float r, float g, float b) {
        super.loadVector(locationSkyColor, new Vector3f(r, g, b));
    }

    /**
     * Loads whether lighting should be faked.
     * 
     * @param useFake ?
     */
    public void loadUseFakeLighting(boolean useFake) {
        super.loadBoolean(locationUseFakeLighting, useFake);
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

    /**
     * Loads shiny values for the shader.
     * 
     * @param damper value
     * @param reflectivity value
     */
    public void loadShineValues(float damper, float reflectivity) {
        super.loadFloat(locationShineDamper, damper);
        super.loadFloat(locationReflectivity, reflectivity);
    }
}
