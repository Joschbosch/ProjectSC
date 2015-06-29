/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.client.gui.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.GraphicalEntity;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.shaders.EntityShader;
import de.projectsc.client.gui.shaders.TerrainShader;
import de.projectsc.client.gui.terrain.TerrainModel;
import de.projectsc.client.gui.tools.Loader;

/**
 * Coordinates rendering of multiple entites.
 * 
 * @author Josch Bosch
 */
public class MasterRenderer {

    private static final float FOV = 90f;

    private static final float NEAR_PLANE = 0.1f;

    private static final float FAR_PLANE = 1000f;

    private static final float SKY_R = 0.54f;

    private static final float SKY_G = 0.62f;

    private static final float SKY_B = 0.69f;

    private Matrix4f projectionMatrix;

    private final EntityShader entityShader;

    private final EntityRenderer entityRenderer;

    private final TerrainShader terrainShader;

    private final TerrainRenderer terrainRenderer;

    private final SkyboxRenderer skyboxRenderer;

    private final Map<TexturedModel, List<GraphicalEntity>> entities = new HashMap<>();

    private final List<TerrainModel> terrains = new ArrayList<>();

    public MasterRenderer(Loader loader) {
        enableCulling();
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
        createProjectionMatrix();
        entityShader = new EntityShader();
        entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
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
     * Render the whole scene with all objects.
     * 
     * @param terrain to render
     * @param staticWorldEntities to render
     * @param dynamicRenderEntities
     * @param lights to render
     * @param camera for view
     * @param elapsedTime since last frame
     * @param clipPlane to clip the world
     */
    public void renderScene(TerrainModel terrain, List<GraphicalEntity> staticWorldEntities, List<GraphicalEntity> dynamicRenderEntities,
        List<Light> lights,
        Camera camera, long elapsedTime, Vector4f clipPlane) {
        processTerrain(terrain);
        for (GraphicalEntity e : staticWorldEntities) {
            processEntity(e);
        }
        for (GraphicalEntity e : dynamicRenderEntities) {
            processEntity(e);
        }
        render(lights, camera, elapsedTime, clipPlane);
    }

    /**
     * General render method .
     * 
     * @param lights to use
     * @param camera to use
     * @param elapsedTime since last frame
     * @param clipPlane to clip the world
     */
    public void render(List<Light> lights, Camera camera, long elapsedTime, Vector4f clipPlane) {
        prepare();
        entityShader.start();
        entityShader.loadClipPlane(clipPlane);
        entityShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        entityShader.loadLights(lights);
        entityShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        entityShader.stop();
        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        skyboxRenderer.render(elapsedTime, camera, SKY_R, SKY_G, SKY_B);
        entities.clear();
        terrains.clear();
    }

    /**
     * Add terrain to be rendered later.
     * 
     * @param terrain to render
     */
    public void processTerrain(TerrainModel terrain) {
        terrains.add(terrain);
    }

    /**
     * Add entity for rendering.
     * 
     * @param entity to render.
     */
    public void processEntity(GraphicalEntity entity) {
        TexturedModel entityModel = entity.getModel();
        List<GraphicalEntity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<GraphicalEntity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    /**
     * Delete everything.
     */
    public void dispose() {
        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
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

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

}
