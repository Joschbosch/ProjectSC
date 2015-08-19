/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.gui.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.render.MasterRenderer;
import de.projectsc.client.gui.render.UIRenderer;
import de.projectsc.client.gui.render.WaterRenderer;
import de.projectsc.client.gui.terrain.water.WaterFrameBuffers;
import de.projectsc.client.gui.terrain.water.WaterTile;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.MousePicker;
import de.projectsc.client.gui.ui.UITexture;
import de.projectsc.core.Terrain;
import de.projectsc.core.TerrainLoader;
import de.projectsc.core.entities.Entity;

/**
 * 
 * State when the game is running (drawing the map etc.).
 * 
 * @author Josch Bosch
 */
public class GameRunning implements State {

    private static final int CLIPPING_PLANE_NOT_RENDERING = 100000;

    private static final Log LOGGER = LogFactory.getLog(GameRunning.class);

    private static final GUIState STATE = GUIState.GAME;

    private Camera camera;

    private MasterRenderer masterRenderer;

    private UIRenderer uiRenderer;

    private Terrain terrain;

    private Map<Integer, Entity> staticEntities;

    private List<UITexture> ui;

    private MousePicker mousePicker;

    private List<WaterTile> waters;

    private WaterFrameBuffers waterfbo;

    private WaterRenderer waterRenderer;

    private boolean wireframeMode = false;

    private boolean renderBoundingBoxes = false;

    private final Map<Long, Entity> movingEntities;

    public GameRunning() {
        movingEntities = new TreeMap<>();
    }

    @Override
    public void initialize() {
        LOGGER.debug("Loading models and light ... ");
        masterRenderer = new MasterRenderer();
        waterfbo = new WaterFrameBuffers();
        waterRenderer = new WaterRenderer(masterRenderer.getProjectionMatrix(), waterfbo);
        loadDemoObjects();
        camera = new Camera();
        mousePicker = new MousePicker(camera, masterRenderer.getProjectionMatrix(), terrain);
        LOGGER.debug("Terrain, models and lights loaded");
    }

    private void loadDemoObjects() {
        loadTerrain("newDataMap");

        ui = new ArrayList<>();
        uiRenderer = new UIRenderer();
        // UITexture uiTex =
        // new UITexture(loader.loadTexture("health.png"), new Vector2f(-0.75f, -0.9f), new
        // Vector2f(0.25f, 0.25f));
        // ui.add(uiTex);
        // UITexture reflectionUI = new UITexture(waterfbo.getReflectionTexture(), new
        // Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        // UITexture refractionUI = new UITexture(waterfbo.getRefractionTexture(), new
        // Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        // ui.add(reflectionUI);
        // ui.add(refractionUI);

    }

    private void loadTerrain(String mapName) {
        LOGGER.debug("Loading terrain " + mapName);
        Terrain terrain = TerrainLoader.loadTerrain(mapName + ".psc");
        terrain.buildNeighborhood();
        terrain.makeStaticObjectsNotWalkable();

        if (staticEntities == null) {
            staticEntities = new TreeMap<>();
        }
    }

    @Override
    public void pause() {
        LOGGER.debug("Pause state" + STATE.name());
    }

    @Override
    public void resume() {
        LOGGER.debug("Resume state" + STATE.name());
    }

    @Override
    public void render(long elapsedTime) {
        input();
        camera.move(elapsedTime);
        mousePicker.update();

        if (waters != null && waters.size() > 0) {
            waterfbo.bindReflectionFrameBuffer();

            float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            masterRenderer.renderScene(terrain.getModel(), (List<Entity>) staticEntities.values(), camera, elapsedTime,
                new Vector4f(0, 1, 0, -waters
                    .get(0).getHeight()));
            camera.getPosition().y += distance;
            camera.invertPitch();

            waterfbo.bindRefractionFrameBuffer();
            masterRenderer.renderScene(terrain.getModel(), (List<Entity>) staticEntities.values(), camera, elapsedTime,
                new Vector4f(0, 0 - 1, 0, waters
                    .get(0).getHeight()));

            waterfbo.unbindCurrentFrameBuffer();
        }
        masterRenderer.renderScene(terrain.getModel(), (List<Entity>) staticEntities.values(), camera, elapsedTime, new Vector4f(0,
            1, 0, CLIPPING_PLANE_NOT_RENDERING));
        if (waters != null && waters.size() > 0) {
            waterRenderer.render(waters, camera, elapsedTime);
        }

        uiRenderer.render(ui);
        // font.drawString(0.0f, 0.0f, "Time : " + elapsedTime, Color.red);

    }

    private void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_O && Keyboard.getEventKeyState()) {
                if (wireframeMode) {
                    wireframeMode = false;
                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                } else {
                    wireframeMode = true;
                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                }
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_B && Keyboard.getEventKeyState()) {
                if (renderBoundingBoxes) {
                    renderBoundingBoxes = false;
                } else {
                    renderBoundingBoxes = true;
                }
            }
        }

    }

    @Override
    public void update() {

    }

    @Override
    public void terminate() {
        LOGGER.debug("Terminate state " + STATE.name());
        Loader.dispose();
        masterRenderer.dispose();
        uiRenderer.dispose();

        waterfbo.dispose();
        waterRenderer.dispose();
    }

    @Override
    public void handleInput(long elapsedTime) {}

    /**
     * Add new entitiy to gui rednering.
     * 
     * @param e new entity
     */
    public void addWorldEntity(Entity e) {
        movingEntities.put(e.getID(), e);
    }

    /**
     * Remove entity.
     * 
     * @param id of entity
     */
    public void removeWorldEntity(int id) {
        movingEntities.remove(id);
    }

}
