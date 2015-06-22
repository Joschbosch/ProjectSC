/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.states;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.content.Map;
import de.projectsc.core.data.messages.GUIMessage;
import de.projectsc.gui.content.GUICommand;
import de.projectsc.gui.content.MiniMap;
import de.projectsc.gui.models.RawModel;
import de.projectsc.gui.models.TexturedModel;
import de.projectsc.gui.objects.Camera;
import de.projectsc.gui.objects.Entity;
import de.projectsc.gui.objects.Light;
import de.projectsc.gui.objects.Player;
import de.projectsc.gui.render.MasterRenderer;
import de.projectsc.gui.terrain.Terrain;
import de.projectsc.gui.textures.ModelTexture;
import de.projectsc.gui.textures.TerrainTexture;
import de.projectsc.gui.textures.TerrainTexturePack;
import de.projectsc.gui.tools.Loader;
import de.projectsc.gui.tools.ModelData;
import de.projectsc.gui.tools.OBJFileLoader;

/**
 * 
 * State when the game is running (drawing the map etc.).
 * 
 * @author Josch Bosch
 */
public class StateGameRunning implements State {

    private static final Log LOGGER = LogFactory.getLog(StateGameRunning.class);

    private static final GUIState STATE = GUIState.GAME;

    private Map currentMap;

    @SuppressWarnings("unused")
    private final BlockingQueue<GUIMessage> outgoingQueue;

    private MiniMap minimap;

    private final BlockingQueue<GUICommand> drawableQueue = new LinkedBlockingQueue<>();

    private Loader loader;

    private final Entity[] entities = new Entity[5];

    private Camera camera;

    private Light light;

    private MasterRenderer masterRenderer;

    private Terrain terrain;

    private Terrain terrain2;

    private Entity farnEntity;

    private Entity grassEntity;

    private Terrain terrain3;

    private Terrain terrain4;

    private Player player;

    public StateGameRunning(BlockingQueue<GUIMessage> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
    }

    @Override
    public void initialize() {
        LOGGER.debug("Loading models and light ... ");
        loader = new Loader();

        ModelData playerData = OBJFileLoader.loadOBJ("terrain/bunny");
        RawModel playerModel =
            loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
        TexturedModel playerTexModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("white.png")));
        loadDemoObjects();
        player = new Player(playerTexModel, new Vector3f(100f, 0f, -50f), 0, 0, 0, 1);
        camera = new Camera(player);
        masterRenderer = new MasterRenderer();

    }

    private void loadDemoObjects() {
        ModelData goatData = OBJFileLoader.loadOBJ("goat");
        RawModel goatModel =
            loader.loadToVAO(goatData.getVertices(), goatData.getTextureCoords(), goatData.getNormals(), goatData.getIndices());
        ModelTexture goatTexture = new ModelTexture(loader.loadTexture("white.png"));
        TexturedModel goatTexturedModel = new TexturedModel(goatModel, goatTexture);
        for (int i = 0; i < 5; i++) {
            entities[i] = new Entity(goatTexturedModel, new Vector3f(-5 + i * 3, 0, -5), 0, 0, 0, 1);
        }

        light = new Light(new Vector3f(0, 0, -0), new Vector3f(1f, 1f, 1f));

        TerrainTexture backgroundTex = new TerrainTexture(loader.loadTexture("terrain/grass.png"));
        TerrainTexture rTex = new TerrainTexture(loader.loadTexture("terrain/mud.png"));
        TerrainTexture gTex = new TerrainTexture(loader.loadTexture("terrain/groundFlower.png"));
        TerrainTexture bTex = new TerrainTexture(loader.loadTexture("terrain/path.png"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTex, rTex, gTex, bTex);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain/blendMap.png"));

        terrain = new Terrain(-1, -1, texturePack, blendMap, loader);
        terrain2 = new Terrain(0, -1, texturePack, blendMap, loader);
        terrain3 = new Terrain(-1, 0, texturePack, blendMap, loader);
        terrain4 = new Terrain(0, 0, texturePack, blendMap, loader);

        farnEntity = loadModel("terrain/fern", "terrain/fern.png", new Vector3f(-5 + 3, 0, -5), 0, 0, 0, 0.1f);
        farnEntity.getModel().getTexture().setFakeLighting(true);
        farnEntity.getModel().getTexture().setTransparent(true);

        grassEntity = loadModel("terrain/grassModel", "terrain/grassTexture.png", new Vector3f(0, 0, -5), 0, 0, 0, 0.1f);
        grassEntity.getModel().getTexture().setTransparent(true);
        grassEntity.getModel().getTexture().setFakeLighting(true);
        LOGGER.debug("Models and light loaded");
    }

    private Entity loadModel(String name, String textureName, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        ModelData data = OBJFileLoader.loadOBJ(name);
        RawModel model =
            loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        ModelTexture texture = new ModelTexture(loader.loadTexture(textureName));
        TexturedModel texturedModel = new TexturedModel(model, texture);
        Entity entity = new Entity(texturedModel, position, rotX, rotY, rotZ, scale);
        return entity;
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
        camera.move();
        player.move(elapsedTime);
        for (int i = 0; i < 5; i++) {
            entities[i].increasePostion(0f, 0f, 0.001f);
            masterRenderer.processEntity(entities[i]);
        }
        masterRenderer.processEntity(farnEntity);
        masterRenderer.processEntity(grassEntity);
        masterRenderer.processEntity(player);
        masterRenderer.processTerrain(terrain);
        masterRenderer.processTerrain(terrain2);
        masterRenderer.processTerrain(terrain3);
        masterRenderer.processTerrain(terrain4);

        masterRenderer.render(light, camera);

    }

    @Override
    public void update() {

    }

    @Override
    public void terminate() {
        LOGGER.debug("Terminate state " + STATE.name());
        loader.dispose();
        masterRenderer.dispose();
    }

    /**
     * Sets the current map to render.
     * 
     * @param map to render
     */
    public void setCurrentMap(Map map) {
        if (minimap != null) {
            minimap.setCurrentMap(map);
            if (currentMap == null) {
                minimap.setCurrentPosition(Display.getWidth() - minimap.getWidth(), Display.getHeight() - minimap.getHeight());
            }
        }
        this.currentMap = map;
    }

    @Override
    public void handleInput(long elapsedTime) {

        handleKeyInput(elapsedTime);

        GUICommand command = drawableQueue.poll();
        while (command != null) {
            if (command.getMessage().equals(GUICommand.CHANGE_LOCATION)) {
                LOGGER.debug("Chaning location");
            }
            command = drawableQueue.poll();

        }
    }

    private void handleKeyInput(long elapsedTime) {
        // camera.updatePosition(elapsedTime);
    }

}
