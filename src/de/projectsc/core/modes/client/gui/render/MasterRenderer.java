/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.modes.client.gui.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.data.BoundingBox;
import de.projectsc.core.data.entities.Entity;
import de.projectsc.core.modes.client.gui.data.Scene;
import de.projectsc.core.modes.client.gui.models.RawModel;
import de.projectsc.core.modes.client.gui.models.TexturedModel;
import de.projectsc.core.modes.client.gui.objects.Camera;
import de.projectsc.core.modes.client.gui.shaders.EntityShader;
import de.projectsc.core.modes.client.gui.shaders.TerrainShader;
import de.projectsc.core.modes.client.gui.shaders.WireFrameShader;

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

    private final WireFrameShader collisionBoxShader;

    private final WireFrameRenderer collisionBoxRenderer;

    private final ParticleRenderer particleRenderer;

    private final boolean showWireFrames = true;

    public MasterRenderer() {
        enableCulling();
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
        createProjectionMatrix();
        entityShader = new EntityShader();
        entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
        collisionBoxShader = new WireFrameShader();
        collisionBoxRenderer = new WireFrameRenderer(collisionBoxShader, projectionMatrix);
        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        billboardRenderer = new BillboardRenderer(projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(projectionMatrix);
        particleRenderer = new ParticleRenderer(projectionMatrix);
        // billboard = new Billboard(loader);
        // billboard.setPosition(new Vector3f(0.0f, 10.0f, 0.0f));
        // billboard.setSize(new Vector2f(2.0f, 2.0f));
        // try {
        // billboard.setImageFile(new
        // File(MasterRenderer.class.getResource("/graphics/lamp.png").toURI()));
        // } catch (URISyntaxException e) {
        // System.err.println("Could not load file");
        // }
        // particleEmitterRainbow =
        // new ParticleEmitter(new Vector3f(10, 10, 0), "particleTexture2.png", 1.0f, true,
        // new BasicParticleConfiguration());
        // particleEmitterRainbow.setGlowy(true);
        // particleEmitterRainbow.setNumberOfParticles(1000);
        // particleEmitterFire =
        // new ParticleEmitter(new Vector3f(10, 10, 0), "particleAtlas.png", 8.0f, false,
        // new BasicParticleConfiguration());

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
     * @param scene to render
     * @param camera for view
     * @param elapsedTime since last frame
     * @param clipPlane to clip the world
     */
    public void renderScene(Scene scene,
        Camera camera, long elapsedTime, Vector4f clipPlane) {
        billboardRenderer.setCamera(camera);
        particleRenderer.setCamera(camera);
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
    public void render(Scene scene, Camera camera, long elapsedTime,
        Vector4f clipPlane) {
        prepare();
        entityShader.start();
        entityShader.loadClipPlane(clipPlane);
        entityShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        entityShader.loadLights(scene.getLights());
        entityShader.loadViewMatrix(camera);
        entityRenderer.render(scene.getEntities());
        entityShader.stop();
        if (showWireFrames) {
            collisionBoxShader.start();
            collisionBoxShader.loadViewMatrix(camera);
            collisionBoxRenderer.render(getBoundingBoxes(scene.getEntities()));
            collisionBoxShader.stop();
        }
        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        terrainShader.loadLights(scene.getLights());
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(scene.getTerrain());
        terrainShader.stop();
        skyboxRenderer.render(elapsedTime, camera, SKY_R, SKY_G, SKY_B);
        billboardRenderer.render(scene.getBillboards());
        particleRenderer.render(scene.getParticles());
    }

    private Map<RawModel, List<BoundingBox>> getBoundingBoxes(Map<TexturedModel, List<Entity>> entities) {
        return new HashMap<>();
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
