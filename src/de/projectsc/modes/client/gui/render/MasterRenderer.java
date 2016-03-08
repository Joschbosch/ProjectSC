/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.render;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.data.objects.Light;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.objects.Camera;
import de.projectsc.modes.client.gui.objects.particles.ParticleMaster;
import de.projectsc.modes.client.gui.settings.GUISettings;
import de.projectsc.modes.client.gui.shaders.EntityShader;
import de.projectsc.modes.client.gui.shaders.TerrainShader;
import de.projectsc.modes.client.gui.shaders.WireFrameShader;
import de.projectsc.modes.client.gui.shadows.ShadowMapMasterRenderer;
import de.projectsc.modes.client.gui.utils.GUIConstants;

/**
 * Coordinates rendering of multiple entites.
 * 
 * @author Josch Bosch
 */
public class MasterRenderer {

    private static final float SKY_R = 0.54f;

    private static final float SKY_G = 0.62f;

    private static final float SKY_B = 0.69f;

    private Matrix4f projectionMatrix;

    private final EntityShader entityShader;

    private final EntityRenderer entityRenderer;

    private final TerrainShader terrainShader;

    private final TerrainRenderer terrainRenderer;

    private final SkyboxRenderer skyboxRenderer;

    private final BillboardRenderer billboardRenderer;

    private final WireFrameShader wireframeShader;

    private final WireFrameRenderer collisionBoxRenderer;

    private final ParticleRenderer particleRenderer;

    private final ShadowMapMasterRenderer shadowRenderer;

    public MasterRenderer() {
        GUISettings.enableCulling();
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
        createProjectionMatrix();
        entityShader = new EntityShader();
        entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
        wireframeShader = new WireFrameShader();
        collisionBoxRenderer = new WireFrameRenderer(wireframeShader, projectionMatrix);
        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        billboardRenderer = new BillboardRenderer(projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(projectionMatrix);
        particleRenderer = new ParticleRenderer(projectionMatrix);
        shadowRenderer = new ShadowMapMasterRenderer();
    }

    /**
     * Prepare rendering for every frame.
     * 
     * @param scene to prepare
     */
    public void prepare(GUIScene scene) {
        if (scene.getSkyColor() == null) {
            GL11.glClearColor(SKY_R, SKY_G, SKY_B, 1);
        } else {
            GL11.glClearColor(scene.getSkyColor().x, scene.getSkyColor().y, scene.getSkyColor().z, 1);
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
    }

    /**
     * Render the whole scene with all objects.
     * 
     * @param scene to render
     * @param camera for view
     * @param elapsedTime since last frame
     * @param clipPlane to clip the world
     */
    public void renderScene(GUIScene scene, Camera camera, long elapsedTime, Vector4f clipPlane) {
        billboardRenderer.setCamera(camera);
        render(scene, camera, elapsedTime, clipPlane);
    }

    /**
     * General render method .
     * 
     * @param scene to render
     * @param camera to use
     * @param elapsedTime since last frame
     * @param clipPlane to clip the world
     */
    public void render(GUIScene scene, Camera camera, long elapsedTime,
        Vector4f clipPlane) {
        prepare(scene);
        entityShader.start();
        entityShader.loadClipPlane(clipPlane);
        if (scene.getSkyColor() == null) {
            entityShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        } else {
            entityShader.loadSkyColor(scene.getSkyColor().x, scene.getSkyColor().y, scene.getSkyColor().z);
        }
        entityShader.loadLights(scene.getLights(), scene.getPositions(), camera.getViewMatrix());
        entityShader.loadViewMatrix(camera);
        entityRenderer.render(scene.getModels(), scene.getPositions(), scene.getRotations(), scene.getScales());
        entityShader.stop();
        Light sun = new Light("", new Vector3f(1000, 1000, 1000), new Vector3f(1, 1, 1), new Vector3f(1, 0, 0), "newSun");
        if (sun != null) {
            shadowRenderer.render(scene, sun, camera);
        }
        if (scene.isDebugMode()) {
            wireframeShader.start();
            wireframeShader.loadViewMatrix(camera);
            collisionBoxRenderer.render(scene.getWireFrames());
            wireframeShader.stop();
        }
        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        if (scene.getSkyColor() == null) {
            terrainShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        } else {
            terrainShader.loadSkyColor(scene.getSkyColor().x, scene.getSkyColor().y, scene.getSkyColor().z);
        }
        terrainShader.loadHighlightedAndSeleced(scene.getHightlightedEntites(), scene.getSelectedEntites());
        terrainShader.loadLights(scene.getLights(), scene.getPositions());
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(scene.getTerrains(), shadowRenderer.getToShadowMapSpaceMatrix(), shadowRenderer.getShadowDistance());
        terrainShader.stop();
        if (scene.renderSkyBox()) {
            if (scene.getFogColor() == null) {
                skyboxRenderer.render(elapsedTime, camera, SKY_R, SKY_G, SKY_B);
            } else {
                skyboxRenderer.render(elapsedTime, camera, scene.getFogColor().x, scene.getFogColor().y, scene.getFogColor().z);
            }
        }
        billboardRenderer.render(scene.getBillboards());
        particleRenderer.render(ParticleMaster.render(), camera.createViewMatrix());

    }

    /**
     * Delete everything.
     */
    public void dispose() {
        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        entityShader.dispose();
        terrainShader.dispose();
        wireframeShader.dispose();
        shadowRenderer.cleanUp();
    }

    // Testing
    public int getShadowMapTexture() {
        return shadowRenderer.getShadowMap();
    }


    private void createProjectionMatrix() {
        projectionMatrix = new Matrix4f();
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float yScale = (float) ((1f / Math.tan(Math.toRadians(GUIConstants.FOV / 2f))));
        float xScale = yScale / aspectRatio;
        float frustumLength = GUIConstants.FAR_PLANE - GUIConstants.NEAR_PLANE;

        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((GUIConstants.FAR_PLANE + GUIConstants.NEAR_PLANE) / frustumLength);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * GUIConstants.NEAR_PLANE * GUIConstants.FAR_PLANE) / frustumLength);
        projectionMatrix.m33 = 0;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

}
