/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
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
import de.projectsc.client.gui.terrain.Terrain;
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
import de.projectsc.core.WorldEntity;

/**
 * 
 * State when the game is running (drawing the map etc.).
 * 
 * @author Josch Bosch
 */
public class GameRunning implements State {

    private static final Log LOGGER = LogFactory.getLog(GameRunning.class);

    private static final GUIState STATE = GUIState.GAME;

    private final BlockingQueue<GUIMessage> outgoingQueue;

    private final BlockingQueue<GUICommand> drawableQueue = new LinkedBlockingQueue<>();

    private Loader loader;

    private Camera camera;

    private List<Light> lights;

    private MasterRenderer masterRenderer;

    private Terrain terrain;

    private Map<Integer, WorldEntity> worldEntities;

    private List<GraphicalEntity> renderEntities;

    private GraphicalEntity player;

    private List<UITexture> ui;

    private List<UITexture> boundingBoxes;

    private UIRenderer uiRenderer;

    private MousePicker mousePicker;

    private List<WaterTile> waters;

    private WaterFrameBuffers waterfbo;

    private WaterRenderer waterRenderer;

    private boolean wireframeMode = false;

    private boolean renderBoundingBoxes = false;

    public GameRunning(BlockingQueue<GUIMessage> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
    }

    @Override
    public void initialize() {
        LOGGER.debug("Loading models and light ... ");
        GameFont.loadFonts();
        loader = new Loader();
        loadEntityModels();
        camera = new Camera(player);
        masterRenderer = new MasterRenderer(loader);
        waterfbo = new WaterFrameBuffers();
        waterRenderer = new WaterRenderer(loader, masterRenderer.getProjectionMatrix(), waterfbo);
        loadDemoObjects();

    }

    private void loadEntityModels() {
        renderEntities = new ArrayList<>();

        for (WorldEntity e : worldEntities.values()) {
            ModelData data = OBJFileLoader.loadOBJ(e.getModel());
            RawModel goatModel =
                loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
            ModelTexture goatTexture = new ModelTexture(loader.loadTexture(e.getTexture()));
            GraphicalEntity graphicalEntity = new GraphicalEntity(e, new TexturedModel(goatModel, goatTexture));
            renderEntities.add(graphicalEntity);
            if (e.getModel().equals("person")) {
                player = graphicalEntity;
            }
        }
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
        lights.add(new Light(new Vector3f(0, 500, 0), new Vector3f(1f, 1f, 1f)));

        ui = new ArrayList<>();
        UITexture uiTex =
            new UITexture(loader.loadTexture("health.png"), new Vector2f(-0.75f, -0.9f), new Vector2f(0.25f, 0.25f));
        uiRenderer = new UIRenderer(loader);
        UITexture reflectionUI = new UITexture(waterfbo.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        UITexture refractionUI = new UITexture(waterfbo.getRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));

        ui.add(uiTex);
        // ui.add(reflectionUI);
        // ui.add(refractionUI);

        LOGGER.debug("Models and light loaded");
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
            masterRenderer.renderScene(terrain, renderEntities, player, lights, camera, elapsedTime, new Vector4f(0, 1, 0, -waters.get(0)
                .getHeight()));
            camera.getPosition().y += distance;
            camera.invertPitch();

            waterfbo.bindRefractionFrameBuffer();
            masterRenderer.renderScene(terrain, renderEntities, player, lights, camera, elapsedTime, new Vector4f(0, 0 - 1, 0, waters
                .get(0)
                .getHeight()));

            waterfbo.unbindCurrentFrameBuffer();
        }
        masterRenderer.renderScene(terrain, renderEntities, player, lights, camera, elapsedTime, new Vector4f(0, 1, 0, 100000));
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

    public void setWorldEntities(Map<Integer, WorldEntity> data) {
        this.worldEntities = data;

    }

}
