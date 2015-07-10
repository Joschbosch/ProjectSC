/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.GUIMessage;
import de.projectsc.client.gui.GUIMessageConstants;
import de.projectsc.client.gui.GameFont;
import de.projectsc.client.gui.content.GUICommand;
import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.GraphicalEntity;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.render.MasterRenderer;
import de.projectsc.client.gui.render.UIRenderer;
import de.projectsc.client.gui.render.WaterRenderer;
import de.projectsc.client.gui.terrain.TerrainModel;
import de.projectsc.client.gui.terrain.water.WaterFrameBuffers;
import de.projectsc.client.gui.terrain.water.WaterTile;
import de.projectsc.client.gui.textures.ModelTexture;
import de.projectsc.client.gui.textures.TerrainTexture;
import de.projectsc.client.gui.textures.TerrainTexturePack;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.ModelData;
import de.projectsc.client.gui.tools.MousePicker;
import de.projectsc.client.gui.tools.OBJFileLoader;
import de.projectsc.client.gui.ui.UITexture;
import de.projectsc.core.Terrain;
import de.projectsc.core.TerrainLoader;
import de.projectsc.core.entities.PlayerEntity;
import de.projectsc.core.entities.WorldEntity;

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

    private final BlockingQueue<GUIMessage> outgoingQueue;

    private final BlockingQueue<GUICommand> drawableQueue = new LinkedBlockingQueue<>();

    private Loader loader;

    private Camera camera;

    private List<Light> lights;

    private MasterRenderer masterRenderer;

    private UIRenderer uiRenderer;

    private TerrainModel terrainModel;

    private Map<Integer, WorldEntity> staticEntities;

    private List<GraphicalEntity> staticRenderEntities;

    private GraphicalEntity player;

    private List<UITexture> ui;

    private MousePicker mousePicker;

    private List<WaterTile> waters;

    private WaterFrameBuffers waterfbo;

    private WaterRenderer waterRenderer;

    private boolean wireframeMode = false;

    private boolean renderBoundingBoxes = false;

    private final Map<Integer, WorldEntity> movingEntities;

    private List<GraphicalEntity> dynamicRenderEntities;

    private Map<String, Integer> textureMap;

    public GameRunning(BlockingQueue<GUIMessage> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
        movingEntities = new TreeMap<>();
    }

    @Override
    public void initialize() {
        LOGGER.debug("Loading models and light ... ");
        GameFont.loadFonts();
        loader = new Loader();
        masterRenderer = new MasterRenderer(loader);
        waterfbo = new WaterFrameBuffers();
        waterRenderer = new WaterRenderer(loader, masterRenderer.getProjectionMatrix(), waterfbo);
        loadDemoObjects();
        staticRenderEntities = new ArrayList<>();
        loadEntityModels(staticRenderEntities, staticEntities);
        dynamicRenderEntities = new ArrayList<>();
        loadEntityModels(dynamicRenderEntities, movingEntities);
        camera = new Camera(player);
        mousePicker = new MousePicker(camera, masterRenderer.getProjectionMatrix(), terrainModel);
        LOGGER.debug("Terrain, models and lights loaded");
    }

    private void loadEntityModels(List<GraphicalEntity> renderList, Map<Integer, WorldEntity> entityList) {

        textureMap = new HashMap<>();
        for (WorldEntity e : entityList.values()) {
            loadGraphicalEntity(renderList, e);
        }
    }

    private GraphicalEntity loadGraphicalEntity(List<GraphicalEntity> renderList, WorldEntity e) {
        ModelData data = OBJFileLoader.loadOBJ(e.getModel());
        RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        int texture = 0 - 1;
        if (textureMap.containsKey(e.getTexture())) {
            texture = textureMap.get(e.getTexture());
        } else {
            texture = loader.loadTexture(e.getTexture());
            textureMap.put(e.getTexture(), texture);
        }
        ModelTexture modelTexture = new ModelTexture(texture);
        GraphicalEntity graphicalEntity = new GraphicalEntity(e, new TexturedModel(model, modelTexture));
        renderList.add(graphicalEntity);
        if (e instanceof PlayerEntity) {
            player = graphicalEntity;
            camera.getPosition().x = player.getPosition().x;
            camera.getPosition().z = player.getPosition().z;
        }
        return graphicalEntity;
    }

    private void loadDemoObjects() {
        loadTerrain("newDataMap");

        ui = new ArrayList<>();
        uiRenderer = new UIRenderer(loader);
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
        TerrainTexture backgroundTex = new TerrainTexture(loader.loadTexture(terrain.getBgTexture()));
        TerrainTexture rTex = new TerrainTexture(loader.loadTexture(terrain.getRTexture()));
        TerrainTexture gTex = new TerrainTexture(loader.loadTexture(terrain.getGTexture()));
        TerrainTexture bTex = new TerrainTexture(loader.loadTexture(terrain.getBgTexture()));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTex, rTex, gTex, bTex);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(TerrainLoader.createBlendMap(terrain)));

        terrainModel = new TerrainModel(terrain, 0, 0, texturePack, blendMap, loader);
        lights = terrain.getStaticLights();
        if (staticEntities == null) {
            staticEntities = new TreeMap<>();
        }
        staticEntities.putAll(terrain.getStaticObjects());
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
            masterRenderer.renderScene(terrainModel, staticRenderEntities, dynamicRenderEntities, lights, camera, elapsedTime,
                new Vector4f(0, 1, 0, -waters
                    .get(0).getHeight()));
            camera.getPosition().y += distance;
            camera.invertPitch();

            waterfbo.bindRefractionFrameBuffer();
            masterRenderer.renderScene(terrainModel, staticRenderEntities, dynamicRenderEntities, lights, camera, elapsedTime,
                new Vector4f(0, 0 - 1, 0, waters
                    .get(0).getHeight()));

            waterfbo.unbindCurrentFrameBuffer();
        }
        masterRenderer.renderScene(terrainModel, staticRenderEntities, dynamicRenderEntities, lights, camera, elapsedTime, new Vector4f(0,
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
            if (Keyboard.getEventKey() == Keyboard.KEY_P && Keyboard.getEventKeyState()) {
                camera.bindToEntity(player);
            }
        }
        if (Mouse.isButtonDown(0)) {
            outgoingQueue.offer(new GUIMessage(GUIMessageConstants.POINT_ON_MAP_CLICKED, mousePicker.getCurrentTerrainPoint()));
        }

    }

    @Override
    public void update() {

    }

    @Override
    public void terminate() {
        LOGGER.debug("Terminate state " + STATE.name());
        loader.dispose();
        masterRenderer.dispose();
        uiRenderer.dispose();

        waterfbo.dispose();
        waterRenderer.dispose();
    }

    @Override
    public void handleInput(long elapsedTime) {
        GUICommand command = drawableQueue.poll();
        while (command != null) {
            if (command.getMessage().equals(GUICommand.CHANGE_LOCATION)) {
                LOGGER.debug("Changing location");
            }
            command = drawableQueue.poll();

        }
    }

    /**
     * Add new entitiy to gui rednering.
     * 
     * @param e new entity
     */
    public void addWorldEntity(WorldEntity e) {
        movingEntities.put(e.getID(), e);
        loadGraphicalEntity(dynamicRenderEntities, e);
    }

    /**
     * Remove entity.
     * 
     * @param id of entity
     */
    public void removeWorldEntity(int id) {
        movingEntities.remove(id);
        GraphicalEntity remove = null;
        for (GraphicalEntity e : dynamicRenderEntities) {
            if (e.getEntity().getID().equals(id)) {
                remove = e;
            }
        }
        dynamicRenderEntities.remove(remove);
    }

}
