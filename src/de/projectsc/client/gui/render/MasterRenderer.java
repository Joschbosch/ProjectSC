/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.client.gui.render;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Billboard;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.shaders.EntityShader;
import de.projectsc.client.gui.shaders.TerrainShader;
import de.projectsc.client.gui.shaders.WireFrameShader;
import de.projectsc.client.gui.terrain.TerrainModel;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.core.components.impl.BoundingComponent;
import de.projectsc.core.components.impl.EmittingLightComponent;
import de.projectsc.core.components.impl.ModelAndTextureComponent;
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

    private Map<RawModel, List<BoundingBox>> boundingBoxes = new HashMap<>();

    private Billboard billboard;

    public MasterRenderer(Loader loader) {
        enableCulling();
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
        createProjectionMatrix();
        entityShader = new EntityShader();
        entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
        collisionBoxShader = new WireFrameShader();
        collisionBoxRenderer = new WireFrameRenderer(collisionBoxShader, projectionMatrix);
        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        billboardRenderer = new BillboardRenderer(loader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);

        billboard = new Billboard(loader);
        billboard.setPosition(new Vector3f(0.0f, 10.0f, 0.0f));
        billboard.setSize(new Vector2f(2.0f, 2.0f));
        try {
            billboard.setImageFile(new File(MasterRenderer.class.getResource("/graphics/lamp.png").toURI()));
        } catch (URISyntaxException e) {
            System.err.println("Could not load file");
        }

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
        billboards.add(billboard);
        billboardRenderer.setCamera(camera);
        processTerrain(terrain);
        for (Entity e : allEntities) {
            billboard.setPosition(Vector3f.add(e.getPosition(), new Vector3f(0, 10.0f, 0), null));
            if (e.getComponent(ModelAndTextureComponent.class) != null
                && e.getComponent(ModelAndTextureComponent.class).getTexturedModel() != null) {
                processEntity(e, e.getComponent(ModelAndTextureComponent.class));
            }
            if (e.getComponent(EmittingLightComponent.class) != null) {
                EmittingLightComponent lightComp = e.getComponent(EmittingLightComponent.class);
                lights.addAll(lightComp.getLights());
            }
            BoundingComponent component = e.getComponent(BoundingComponent.class);
            if (component != null) {
                processBoundingBox(component);
            }
        }

        render(lights, billboards, camera, elapsedTime, clipPlane);
    }

    /**
     * General render method .
     * 
     * @param lights to use
     * @param billboards to render
     * @param camera to use
     * @param elapsedTime since last frame
     * @param clipPlane to clip the world
     */
    public void render(List<Light> lights, List<Billboard> billboards, Camera camera, long elapsedTime, Vector4f clipPlane) {
        prepare();
        entityShader.start();
        entityShader.loadClipPlane(clipPlane);
        entityShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        entityShader.loadLights(lights);
        entityShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        entityShader.stop();
        collisionBoxShader.start();
        collisionBoxShader.loadViewMatrix(camera);
        collisionBoxRenderer.render(boundingBoxes);
        collisionBoxShader.stop();
        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(SKY_R, SKY_G, SKY_B);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        skyboxRenderer.render(elapsedTime, camera, SKY_R, SKY_G, SKY_B);
        billboardRenderer.render(billboards);

        entities.clear();
        terrains.clear();
        boundingBoxes.clear();
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
     * @param e entity
     * @param component to render.
     */
    public void processEntity(Entity e, ModelAndTextureComponent component) {
        TexturedModel entityModel = component.getTexturedModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(e);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(e);
            entities.put(entityModel, newBatch);
        }
    }

    private void processBoundingBox(BoundingComponent component) {
        if (component.getBox() != null) {
            RawModel model = component.getBox().getModel();
            List<BoundingBox> batch = boundingBoxes.get(model);
            if (batch != null) {
                batch.add(component.getBox());
            } else {
                List<BoundingBox> newBatch = new ArrayList<>();
                newBatch.add(component.getBox());
                boundingBoxes.put(model, newBatch);
            }
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
