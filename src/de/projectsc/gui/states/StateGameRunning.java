/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.states;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import de.projectsc.core.data.content.Map;
import de.projectsc.core.data.messages.GUIMessage;
import de.projectsc.gui.GameFont;
import de.projectsc.gui.content.GUICommand;
import de.projectsc.gui.content.MiniMap;
import de.projectsc.gui.models.RawModel;
import de.projectsc.gui.models.TexturedModel;
import de.projectsc.gui.objects.Camera;
import de.projectsc.gui.objects.Entity;
import de.projectsc.gui.objects.Light;
import de.projectsc.gui.objects.Player;
import de.projectsc.gui.render.MasterRenderer;
import de.projectsc.gui.render.UIRenderer;
import de.projectsc.gui.terrain.Terrain;
import de.projectsc.gui.textures.ModelTexture;
import de.projectsc.gui.textures.TerrainTexture;
import de.projectsc.gui.textures.TerrainTexturePack;
import de.projectsc.gui.tools.Loader;
import de.projectsc.gui.tools.ModelData;
import de.projectsc.gui.tools.MousePicker;
import de.projectsc.gui.tools.OBJFileLoader;
import de.projectsc.gui.ui.UITexture;

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

    private Camera camera;

    private List<Light> lights;

    private MasterRenderer masterRenderer;

    private Terrain terrain;

    private List<Entity> worldEntities;

    private Player player;

    private List<UITexture> ui;

    private UIRenderer uiRenderer;

    private MousePicker mousePicker;

    private Entity lamp;

    private TrueTypeFont font;

    private Entity goat;

    public StateGameRunning(BlockingQueue<GUIMessage> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
    }

    @Override
    public void initialize() {
        LOGGER.debug("Loading models and light ... ");
        GameFont.loadFonts();
        font = GameFont.getFont(GameFont.GLOBAL, Font.PLAIN, 13, true);
        loader = new Loader();
        loadPlayer();
        camera = new Camera(player);
        masterRenderer = new MasterRenderer(loader);
        loadDemoObjects();
    }

    private void loadPlayer() {
        ModelData playerData = OBJFileLoader.loadOBJ("person");
        RawModel playerModel =
            loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
        TexturedModel playerTexModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("person.png")));
        player = new Player(playerTexModel, new Vector3f(1f, 0f, -50f), 0, 0, 0, 1.4f);
        player.getModel().getTexture().setReflectivity(2f);
    }

    private void loadDemoObjects() {
        TerrainTexture backgroundTex = new TerrainTexture(loader.loadTexture("terrain/grass.png"));
        TerrainTexture rTex = new TerrainTexture(loader.loadTexture("terrain/mud.png"));
        TerrainTexture gTex = new TerrainTexture(loader.loadTexture("terrain/groundFlower.png"));
        TerrainTexture bTex = new TerrainTexture(loader.loadTexture("terrain/path.png"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTex, rTex, gTex, bTex);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain/blendMap.png"));

        terrain = new Terrain(-0.5f, -0.5f, texturePack, blendMap, loader, "heightmap.png");
        mousePicker = new MousePicker(camera, masterRenderer.getProjectionMatrix(), terrain);

        lights = new ArrayList<>();
        lights.add(new Light(new Vector3f(0, 1000, 0), new Vector3f(1f, 1f, 1f)));
        lights.add(new Light(new Vector3f(185, terrain.getHeightOfTerrain(185, -293) + 13f, -293), new Vector3f(2f, 0f, 0f),
            new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(370, terrain.getHeightOfTerrain(370, -300) + 13f, -300), new Vector3f(0f, 2f, 2f), new Vector3f(
            1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(293, terrain.getHeightOfTerrain(293, -305) + 13f, -305), new Vector3f(2f, 2f, 0f), new Vector3f(
            1, 0.01f, 0.002f)));

        worldEntities = new ArrayList<>();
        ModelData goatData = OBJFileLoader.loadOBJ("goat");
        RawModel goatModel =
            loader.loadToVAO(goatData.getVertices(), goatData.getTextureCoords(), goatData.getNormals(), goatData.getIndices());
        ModelTexture goatTexture = new ModelTexture(loader.loadTexture("white.png"));
        TexturedModel goatTexturedModel = new TexturedModel(goatModel, goatTexture);
        goat = new Entity(goatTexturedModel, new Vector3f(-5, terrain.getHeightOfTerrain(-5, -5), -5), 0,
            0, 0, 7f);
        worldEntities.add(goat);
        for (int i = 1; i < 5; i++) {
            worldEntities.add(new Entity(goatTexturedModel, new Vector3f(-5 + i * 10, terrain.getHeightOfTerrain(-5 + i * 10, -5), -5), 0,
                0, 0, 7f));
        }

        addWorldModel(100, "terrain/fern", "terrain/fernTextureAtlas.png", 1f, 2);
        addWorldModel(100, "terrain/grassModel", "terrain/grassTexture.png", 1f, 1);
        addWorldModel(50, "terrain/tree", "terrain/tree.png", 15f, 1);
        addWorldModel(50, "terrain/lowPolyTree", "terrain/lowPolyTree.png", 2f, 1);
        addWorldModel(100, "terrain/fern", "terrain/flower.png", 1f, 1);

        TexturedModel lampmodel = loadModel("lamp", "lamp.png", 1);
        lampmodel.getTexture().setFakeLighting(true);
        lamp = new Entity(lampmodel, new Vector3f(185, terrain.getHeightOfTerrain(185, -293), -293), 0, 0, 0, 1);
        worldEntities.add(lamp);
        worldEntities.add(new Entity(lampmodel, new Vector3f(370, terrain.getHeightOfTerrain(370, -300), -300), 0, 0, 0, 1));
        worldEntities.add(new Entity(lampmodel, new Vector3f(293, terrain.getHeightOfTerrain(293, -305), -305), 0, 0, 0, 1));

        ui = new ArrayList<>();
        UITexture uiTex =
            new UITexture(loader.loadTexture("health.png"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        ui.add(uiTex);
        uiRenderer = new UIRenderer(loader);

        LOGGER.debug("Models and light loaded");
    }

    private void addWorldModel(int count, String objectName, String objectTexture, float scale, int numberOfRows) {
        TexturedModel model = loadModel(objectName, objectTexture, numberOfRows);
        model.getTexture().setFakeLighting(true);
        model.getTexture().setTransparent(true);
        for (int i = 0; i < count; i++) {
            float randomX = (float) (Math.random() * Terrain.SIZE - Terrain.SIZE / 2);
            float randomZ = (float) (Math.random() * Terrain.SIZE - Terrain.SIZE / 2);
            worldEntities.add(new Entity(model, (int) (Math.random() * numberOfRows * numberOfRows), new Vector3f(randomX, terrain
                .getHeightOfTerrain(randomX, randomZ), randomZ), 0, 0, 0,
                scale));
        }

    }

    private TexturedModel loadModel(String name, String textureName, int numberOfRows) {

        ModelData fernData = OBJFileLoader.loadOBJ(name);
        RawModel fernModel =
            loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), fernData.getNormals(), fernData.getIndices());
        ModelTexture fernTexture = new ModelTexture(loader.loadTexture(textureName));
        fernTexture.setNumberOfRows(numberOfRows);
        TexturedModel fernTexturedModel = new TexturedModel(fernModel, fernTexture);
        return fernTexturedModel;
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
        player.move(elapsedTime, terrain);
        camera.move();
        mousePicker.update();

        goat.setPosition(new Vector3f(goat.getPosition().x, terrain.getHeightOfTerrain(goat.getPosition().x, goat.getPosition().z + 1),
            goat.getPosition().z + 1));
        Vector3f currentTerrainPoint = mousePicker.getCurrentTerrainPoint();
        if (currentTerrainPoint != null) {
            lamp.setPosition(currentTerrainPoint);
            lights.get(1).setPosition(new Vector3f(currentTerrainPoint.x, currentTerrainPoint.y + 13, currentTerrainPoint.z));

        }
        masterRenderer.processTerrain(terrain);
        for (Entity e : worldEntities) {
            masterRenderer.processEntity(e);
        }
        masterRenderer.processEntity(player);
        masterRenderer.render(lights, camera, elapsedTime);

        uiRenderer.render(ui);
        font.drawString(0.0f, 0.0f, "Time : " + elapsedTime, Color.red);

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
        GUICommand command = drawableQueue.poll();
        while (command != null) {
            if (command.getMessage().equals(GUICommand.CHANGE_LOCATION)) {
                LOGGER.debug("Chaning location");
            }
            command = drawableQueue.poll();

        }
    }

}
