/*
 * Copyright (C) 2015 
 */

package de.projectsc.gui.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.gui.objects.Camera;

public class SkyboxShader extends Shader {

    private static final double TIMER_1_SEC = 1000.0;

    private static final String VERTEX_FILE = "skyboxShader.vert";

    private static final String FRAGMENT_FILE = "skyboxShader.frag";

    private static final float ROTATION_SPEED = 0.5f;

    private float currentRotationSpeed;

    private int locationProjectionMatrix;

    private int locationViewMatrix;

    private int locationFogColor;

    private int locationCubeMap;

    private int locationCubeMap2;

    private int locationBlendFactor;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationViewMatrix = super.getUniformLocation("viewMatrix");
        locationFogColor = super.getUniformLocation("fogColor");
        locationCubeMap = super.getUniformLocation("cubeMap");
        locationCubeMap2 = super.getUniformLocation("cubeMap2");
        locationBlendFactor = super.getUniformLocation("blendFactor");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(locationProjectionMatrix, matrix);
    }

    public void loadBlendFactor(float factor) {
        super.loadFloat(locationBlendFactor, factor);
    }

    public void connectTextureUnits() {
        super.loadInt(locationCubeMap, 0);
        super.loadInt(locationCubeMap2, 1);
    }

    public void loadProjectionMatrix(float blend) {
        super.loadFloat(locationBlendFactor, blend);
    }

    public void loadViewMatrix(Camera camera, long delta) {
        Matrix4f matrix = camera.createViewMatrix();
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        matrix.m33 = 1;
        currentRotationSpeed += ROTATION_SPEED * delta / TIMER_1_SEC;
        Matrix4f.rotate((float) Math.toRadians(currentRotationSpeed), new Vector3f(0, 1, 0), matrix, matrix);
        super.loadMatrix(locationViewMatrix, matrix);
    }

    public void loadFogColor(float r, float g, float b) {
        loadVector(locationFogColor, new Vector3f(r, g, b));
    }

}
