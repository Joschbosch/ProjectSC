/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.client.gui.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Billboard;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.objects.ParticleEmitter;
import de.projectsc.client.gui.shaders.EntityShader;
import de.projectsc.client.gui.shaders.TerrainShader;
import de.projectsc.client.gui.shaders.WireFrameShader;
import de.projectsc.client.gui.terrain.TerrainModel;
import de.projectsc.client.gui.text.TextMaster;
import de.projectsc.core.components.Component;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.utils.BoundingBox;

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

    private final Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    private final List<TerrainModel> terrains = new ArrayList<>();

    private final WireFrameShader collisionBoxShader;

    private final WireFrameRenderer collisionBoxRenderer;

    private final Map<RawModel, List<BoundingBox>> boundingBoxes = new HashMap<>();

    // private final ParticleEmitter particleEmitterRainbow;

    private final ParticleRenderer particleRenderer;

    private final boolean showWireFrames = true;

    //
    // private final ParticleEmitter particleEmitterFire;

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
     * @param terrain to render
     * @param allEntities all entities
     * @param camera for view
     * @param elapsedTime since last frame
     * @param clipPlane to clip the world
     */
    public void renderScene(TerrainModel terrain, List<Entity> allEntities,
        Camera camera, long elapsedTime, Vector4f clipPlane) {
        List<Light> lights = new LinkedList<>();
        List<Billboard> billboards = new LinkedList<>();
        List<ParticleEmitter> particles = new LinkedList<>();

        // billboards.add(billboard);
        billboardRenderer.setCamera(camera);
        particleRenderer.setCamera(camera);

        // TEST CODE!
        // particles.add(particleEmitterRainbow);
        // particles.add(particleEmitterFire);
        // particleEmitterRainbow.setCameraPostion(camera.getPosition());
        // particleEmitterRainbow.update();
        // particleEmitterFire.setCameraPostion(camera.getPosition());
        // particleEmitterFire.update();

        processTerrain(terrain);
        for (Entity e : allEntities) {
            for (Component c : e.getComponents().values()) {
                c.render(e, entities, boundingBoxes, lights, billboards, particles, camera, elapsedTime);
            }
        }

        render(lights, billboards, particles, camera, elapsedTime, clipPlane);
    }

    /**
     * General render method .
     * 
     * @param lights to use
     * @param billboards to render+
     * @param particles to render
     * @param camera to use
     * @param elapsedTime since last frame
     * @param clipPlane to clip the world
     */
    public void render(List<Light> lights, List<Billboard> billboards, List<ParticleEmitter> particles, Camera camera, long elapsedTime,
        Vector4f clipPlane) {
        prepare();
        entityShader.start();
        entityShader.loadClipPlane(clipPlane);
        entityShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        entityShader.loadLights(lights);
        entityShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        entityShader.stop();
        if (showWireFrames) {
            collisionBoxShader.start();
            collisionBoxShader.loadViewMatrix(camera);
            collisionBoxRenderer.render(boundingBoxes);
            collisionBoxShader.stop();
        }
        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        skyboxRenderer.render(elapsedTime, camera, SKY_R, SKY_G, SKY_B);
        billboardRenderer.render(billboards);
        particleRenderer.render(particles);
        entities.clear();
        terrains.clear();
        boundingBoxes.clear();
        TextMaster.render();
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
