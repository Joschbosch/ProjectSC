/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.gui.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.gui.models.TexturedModel;
import de.projectsc.gui.objects.Camera;
import de.projectsc.gui.objects.Entity;
import de.projectsc.gui.objects.Light;
import de.projectsc.gui.shaders.EntityShader;
import de.projectsc.gui.shaders.TerrainShader;
import de.projectsc.gui.terrain.Terrain;

/**
 * Coordinates rendering of multiple entites.
 * 
 * @author Josch Bosch
 */
public class MasterRenderer {

    private static final float FOV = 90f;

    private static final float NEAR_PLANE = 0.1f;

    private static final float FAR_PLANE = 1000f;

    private static final float SKY_R = 0.5f;

    private static final float SKY_G = 0.7f;

    private static final float SKY_B = 0.99f;

    private Matrix4f projectionMatrix;

    private final EntityShader entityShader;

    private final EntityRenderer entityRenderer;

    private final TerrainShader terrainShader;

    private final TerrainRenderer terrainRenderer;

    private final Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    private final List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer() {
        enableCulling();
        createProjectionMatrix();
        entityShader = new EntityShader();
        entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    /**
     * Prepare rendering for every frame.
     */
    public void prepare() {
        GL11.glClearColor(SKY_R, SKY_G, SKY_B, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * General render method .
     * 
     * @param light to use
     * @param camera to use
     */
    public void render(Light light, Camera camera) {
        prepare();
        entityShader.start();
        entityShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        entityShader.loadLight(light);
        entityShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        entityShader.stop();
        terrainShader.start();
        terrainShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        terrainShader.loadLight(light);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        entities.clear();
        terrains.clear();

    }

    /**
     * Add terrain to be rendered later.
     * 
     * @param terrain to render
     */
    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    /**
     * Add entity for rendering.
     * 
     * @param entity to render.
     */
    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    /**
     * Delete everything.
     */
    public void dispose() {
        entityShader.dispose();
        terrainShader.dispose();
    }

    /**
     * Enable culling for not render too much back faces.
     */
    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    /**
     * Disable culling for rendering transparent faces.
     */
    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float yScale = (float) ((1.f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float xScale = yScale / aspectRatio;
        float frustrumLength = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();

        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustrumLength);
        projectionMatrix.m23 = 0 - 1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustrumLength);
        projectionMatrix.m33 = 0;
    }
}
