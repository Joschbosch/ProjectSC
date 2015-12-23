/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.render;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.objects.Camera;
import de.projectsc.modes.client.gui.objects.particles.ParticleMaster;
import de.projectsc.modes.client.gui.shaders.EntityShader;
import de.projectsc.modes.client.gui.shaders.TerrainShader;
import de.projectsc.modes.client.gui.shaders.WireFrameShader;

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

    private final BillboardRenderer billboardRenderer;

    private final WireFrameShader wireframeShader;

    private final WireFrameRenderer collisionBoxRenderer;

    private final ParticleRenderer particleRenderer;

    public MasterRenderer() {
        enableCulling();
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
        entityShader.loadLights(scene.getLights());
        entityShader.loadViewMatrix(camera);
        entityRenderer.render(scene.getModels(), scene.getPositions(), scene.getRotations(), scene.getScales(),
            scene.getSelectedEntites(), scene.getHightlightedEntites());
        entityShader.stop();
        if (scene.isWireframeEnable()) {
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
        terrainShader.loadLights(scene.getLights());
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(scene.getTerrains());
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
