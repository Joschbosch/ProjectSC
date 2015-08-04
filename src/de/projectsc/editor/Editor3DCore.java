/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.editor;

import java.awt.Canvas;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.render.NewMasterRenderer;
import de.projectsc.client.gui.terrain.TerrainModel;
import de.projectsc.client.gui.textures.TerrainTexture;
import de.projectsc.client.gui.textures.TerrainTexturePack;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.MousePicker;
import de.projectsc.core.Terrain;
import de.projectsc.core.TerrainLoader;
import de.projectsc.core.Tile;
import de.projectsc.core.components.ComponentType;
import de.projectsc.core.components.impl.EmittingLightComponent;
import de.projectsc.core.components.impl.ModelAndTextureComponent;
import de.projectsc.core.components.impl.MovingComponent;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.entities.WorldEntity;

/**
 * Core class for the GUI.
 * 
 * @author Josch Bosch
 */
public class Editor3DCore implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(Editor3DCore.class);

    private boolean running;

    private final Canvas displayParent;

    private final int width;

    private final int height;

    private Loader loader;

    private EditorCamera camera;

    private MousePicker mousePicker;

    private TerrainModel terrainModel;

    private final BlockingQueue<String> messageQueue;

    private NewMasterRenderer masterRenderer;

    private final List<Entity> entities = new LinkedList<>();

    private Entity entity;

    private EditorData editorData;

    private ModelAndTextureComponent modelComponent;

    private BlockingQueue<String> incomingQueue;

    private Light sun;

    public Editor3DCore(Canvas displayParent, int width, int height, BlockingQueue<String> messageQueue) {
        incomingQueue = new LinkedBlockingQueue<>();
        this.displayParent = displayParent;
        this.width = width;
        this.height = height;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        running = true;
        loadEntity();
        try {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.setTitle("Project SC Entity Editor");
            Display.setVSyncEnabled(true);
            Display.setParent(displayParent);
            Display.create();
            initGL();
        } catch (LWJGLException e) {
        }
        loader = new Loader();

        camera = new EditorCamera(null);
        camera.setPosition(0f, 15f, 10f);
        camera.setYaw(0);
        masterRenderer = new NewMasterRenderer(loader);

        Tile[][] tiles = new Tile[100][100];

        Entity lightEntity = new Entity(-2);
        lightEntity.setPosition(new Vector3f(0, 0, 0));
        EmittingLightComponent lightComponent = new EmittingLightComponent();
        sun = new Light(new Vector3f(0.0f, 100.0f, 100.0f), new Vector3f(1.0f, 1.0f, 1.0f), "sun");
        lightComponent.addLight(lightEntity, sun);
        lightEntity.addComponent(lightComponent);
        entities.add(lightEntity);
        Terrain t =
            new Terrain(tiles, "terrain/grass.png", "terrain/grass.png", "terrain/grass.png", "terrain/grass.png", new LinkedList<>(),
                new HashMap<Integer, WorldEntity>());

        TerrainTexture backgroundTex = new TerrainTexture(loader.loadTexture("terrain/grass.png"));
        TerrainTexture rTex = new TerrainTexture(loader.loadTexture("terrain/grass.png"));
        TerrainTexture gTex = new TerrainTexture(loader.loadTexture("terrain/grass.png"));
        TerrainTexture bTex = new TerrainTexture(loader.loadTexture("terrain/grass.png"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTex, rTex, gTex, bTex);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(TerrainLoader.createBlendMap(t)));

        terrainModel = new TerrainModel(t, -0.5f, -0.5f, texturePack, blendMap, loader);

        mousePicker = new MousePicker(camera, masterRenderer.getProjectionMatrix(), terrainModel);

        gameLoop();
    }

    protected void initGL() {}

    public void updateData(EditorData data) {
        if (entity != null) {
            entity.setScale(editorData.getScale());
        }
        if (modelComponent != null) {
            modelComponent.setFakeLighting(editorData.isFakeLighting());
            modelComponent.setIsTransparent(editorData.isTransparent());
            modelComponent.setReflectivity(editorData.getReflectivity());
            modelComponent.setShineDamper(editorData.getShineDamper());
            modelComponent.setNumberOfRows(editorData.getNumColums());
        }
    }

    public void loadEntity() {
        if (editorData != null) {
            entity = new Entity(editorData.getId());
            entity.setPosition(new Vector3f(0, 0, 0));
            entity.setRotation(new Vector3f(0, 0, 0));
            entities.add(entity);
            if (editorData.getModelFile() != null) {
            }
        } else {
            if (entity != null) {
                entities.remove(entity);
            }
            entity = null;
        }
    }

    public void triggerLoadModel() {
        incomingQueue.offer("loadModel");
    }

    private void loadModel() {
        modelComponent = new ModelAndTextureComponent();
        try {
            modelComponent.loadModel(loader, editorData.getModelFile(), new File(Editor3DCore.class.getResource("/white.png").toURI()));
        } catch (URISyntaxException e) {
            LOGGER.error(e);
        }

        entity.addComponent(modelComponent);
        MovingComponent moving = new MovingComponent();
        // moving.setCurrentSpeed(0.01f);
        entity.addComponent(moving);
    }

    public void updateTexture() {
        if (modelComponent != null) {
            modelComponent.loadAndApplyTexture(loader, editorData.getTextureFile());
            updateData(editorData);
        }
    }

    protected void gameLoop() {

        long time = System.currentTimeMillis();
        int timer = 1500;
        while (running) {
            long now = System.currentTimeMillis();
            long delta = now - time;
            time = now;
            readMessages();
            camera.move(delta);
            if (editorData.isLightAtCameraPostion()) {
                sun.setPosition(camera.getPosition());
            }
            if (editorData.isCycleTextures()) {
                timer -= delta;
                if (timer <= 0) {
                    timer = 1500;
                    ModelAndTextureComponent component = entity.getComponent(ModelAndTextureComponent.class);
                    if (entity != null && component != null) {
                        component.setTextureIndex((component.getTextureIndex() + 1)
                            % (editorData.getNumColums() * editorData.getNumColums()));
                    }
                }
            }
            for (ComponentType type : ComponentType.values()) {
                for (Entity e : entities) {
                    e.update(type);
                }
            }
            if (terrainModel != null) {
                camera.move(delta);
                mousePicker.update();
                masterRenderer.renderScene(terrainModel, entities,
                    camera, delta, new Vector4f(0, 1, 0, 100000));
            } else {
                if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                    GL11.glClearColor(1f, 0, 0, 0);
                } else {
                    GL11.glClearColor(1f, 1f, 0f, 0f);
                }
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }
            String message = messageQueue.poll();
            if (message != null) {
                loadMap();
            }
            Display.sync(60);
            Display.update();
        }
        Display.destroy();
    }

    private void readMessages() {
        while (!incomingQueue.isEmpty()) {
            String msg = incomingQueue.poll();
            if (msg.equals("loadModel")) {
                loadModel();
            }
            if (msg.equals("updateTexture")) {
                updateTexture();
            }
        }
    }

    /**
     * Stop.
     */
    public void stop() {
        running = false;
    }

    public void loadMap() {

        // loadTerrain("map");
    }

    private void loadTerrain(String mapName) {
        Terrain terrain = TerrainLoader.loadTerrain(mapName + ".psc");
        TerrainTexture backgroundTex = new TerrainTexture(loader.loadTexture(terrain.getBgTexture()));
        TerrainTexture rTex = new TerrainTexture(loader.loadTexture(terrain.getRTexture()));
        TerrainTexture gTex = new TerrainTexture(loader.loadTexture(terrain.getGTexture()));
        TerrainTexture bTex = new TerrainTexture(loader.loadTexture(terrain.getBgTexture()));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTex, rTex, gTex, bTex);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(TerrainLoader.createBlendMap(terrain)));

        terrainModel = new TerrainModel(terrain, 0, 0, texturePack, blendMap, loader);
        // lights = terrain.getStaticLights();
        // if (staticEntities == null) {
        // staticEntities = new TreeMap<>();
        // }
        // staticEntities.putAll(terrain.getStaticObjects());
    }

    public void setEditorData(EditorData data) {
        this.editorData = data;
    }

    public void updateData() {
        if (editorData != null) {
            camera.setRotateCamera(editorData.isRotateCamera());
            updateData(editorData);
        }
    }

    public void triggerUpdateTexture() {
        incomingQueue.offer("updateTexture");
    }

    public void addComponent(String component) {
        if (EmittingLightComponent.name.equals(component)) {
            EmittingLightComponent lightComponent = new EmittingLightComponent();
            lightComponent.createAndAddLight(entity, new Vector3f(0.0f, 15f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), "blood");
            entity.addComponent(lightComponent);
        }
        if (MovingComponent.name.equals(component)) {
            entity.addComponent(new MovingComponent());
        }
    }

    public void removeComponent(String component) {
        entity.removeComponent(component);
    }
}
