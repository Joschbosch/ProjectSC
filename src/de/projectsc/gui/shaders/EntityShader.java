/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.shaders;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.gui.objects.Camera;
import de.projectsc.gui.objects.Light;

/**
 * A shader program that does not much.
 * 
 * @author Josch Bosch
 */
public class EntityShader extends Shader {

    private static final int MAX_LIGHTS = 6;

    private static final Log LOGGER = LogFactory.getLog(EntityShader.class);

    private static final String VERTEX_FILE = "entityShader.vert";

    private static final String FRAGMENT_FILE = "entityShader.frag";

    private int locationTransformationMatrix;

    private int locationProjectionMatrix;

    private int locationViewMatrix;

    private int[] locationLightPosition;

    private int[] locationLightColor;

    private int[] locationAttenuation;

    private int locationShineDamper;

    private int locationUseFakeLighting;

    private int locationReflectivity;

    private int locationSkyColor;

    private int locationNumberOfRows;

    private int locationOffset;

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
        locationShineDamper = super.getUniformLocation("shineDamper");
        locationReflectivity = super.getUniformLocation("reflectivity");
        locationUseFakeLighting = super.getUniformLocation("useFakeLighting");
        locationSkyColor = super.getUniformLocation("skyColor");
        locationNumberOfRows = super.getUniformLocation("numberOfRows");
        locationOffset = super.getUniformLocation("offset");
        locationLightPosition = new int[MAX_LIGHTS];
        locationLightColor = new int[MAX_LIGHTS];
        locationAttenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            locationLightPosition[i] = getUniformLocation("lightPosition[" + i + "]");
            locationLightColor[i] = getUniformLocation("lightColor[" + i + "]");
            locationAttenuation[i] = getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void loadNumberRows(int number) {
        loadFloat(locationNumberOfRows, number);
    }

    public void loadOffset(float x, float y) {
        loadVector(locationOffset, new Vector2f(x, y));
    }

    /**
     * Give current color for the sky to the shader.
     * 
     * @param r value
     * @param g value
     * @param b value
     */
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
     * Loads up lights to the shader.
     *
     * @param lights to load
     */
    public void loadLights(List<Light> lights) {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                loadVector(locationLightPosition[i], lights.get(i).getPosition());
                loadVector(locationLightColor[i], lights.get(i).getColor());
                loadVector(locationAttenuation[i], lights.get(i).getAttenuation());
            } else {
                loadVector(locationLightPosition[i], new Vector3f(0.0f, 0.0f, 0.0f));
                loadVector(locationLightColor[i], new Vector3f(0.0f, 0.0f, 0.0f));
                loadVector(locationAttenuation[i], new Vector3f(1.0f, 0.0f, 0.0f));
            }
        }

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
