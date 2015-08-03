/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.editor;

import java.awt.Canvas;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.render.NewEntityRenderer;
import de.projectsc.client.gui.render.NewMasterRenderer;
import de.projectsc.client.gui.shaders.EntityShader;
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

    private boolean running;

    private final Canvas displayParent;

    private final int width;

    private final int height;

    private Loader loader;

    private NewEntityRenderer entityRenderer;

    private Camera camera;

    private MousePicker mousePicker;

    private TerrainModel terrainModel;

    private final BlockingQueue<String> messageQueue;

    private NewMasterRenderer masterRenderer;

    private final List<Entity> entities = new LinkedList<>();

    private Entity firstEntity;

    public Editor3DCore(Canvas displayParent, int width, int height, BlockingQueue<String> messageQueue) {

        this.displayParent = displayParent;
        this.width = width;
        this.height = height;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        running = true;
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

        camera = new Camera(null);
        EntityShader shader = new EntityShader();
        masterRenderer = new NewMasterRenderer(loader);

        entityRenderer = new NewEntityRenderer(shader, masterRenderer.getProjectionMatrix());

        Tile[][] tiles = new Tile[1000][1000];
        List<Light> lights = new LinkedList<>();
        lights.add(new Light(new Vector3f(0.0f, 100.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), "sun"));
        Terrain t =
            new Terrain(tiles, "terrain/mud.png", "terrain/mud.png", "terrain/mud.png", "terrain/mud.png", lights,
                new HashMap<Integer, WorldEntity>());

        TerrainTexture backgroundTex = new TerrainTexture(loader.loadTexture("terrain/mud.png"));
        TerrainTexture rTex = new TerrainTexture(loader.loadTexture("terrain/mud.png"));
        TerrainTexture gTex = new TerrainTexture(loader.loadTexture("terrain/mud.png"));
        TerrainTexture bTex = new TerrainTexture(loader.loadTexture("terrain/mud.png"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTex, rTex, gTex, bTex);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(TerrainLoader.createBlendMap(t)));

        terrainModel = new TerrainModel(t, -0.5f, -0.5f, texturePack, blendMap, loader);

        mousePicker = new MousePicker(camera, masterRenderer.getProjectionMatrix(), terrainModel);

        firstEntity = new Entity(10000);
        firstEntity.setPosition(new Vector3f(0, 0, 0));
        firstEntity.setRotation(new Vector3f(0, 0, 0));
        firstEntity.setScale(1.0f);
        ModelAndTextureComponent modelComponent = new ModelAndTextureComponent();
        modelComponent.loadModel(loader, firstEntity);
        firstEntity.addComponent(modelComponent);
        firstEntity.setScale(1.0f);

        MovingComponent moving = new MovingComponent();
        // moving.setCurrentSpeed(0.01f);
        firstEntity.addComponent(moving);
        entities.add(firstEntity);
        gameLoop();
    }

    protected void initGL() {}

    protected void gameLoop() {

        long time = System.currentTimeMillis();
        while (running) {
            long now = System.currentTimeMillis();
            long delta = now - time;
            time = now;
            Display.update();
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
        }
        Display.destroy();
    }

    /**
     * Stop.
     */
    public void stop() {
        running = false;
    }

    public void loadMap() {
        EmittingLightComponent lightComponent = new EmittingLightComponent();
        Light l = new Light(new Vector3f(0f, 100f, 0f), new Vector3f(1.0f, 1.0f, 1.0f), "oneLight");
        lightComponent.addLight(firstEntity, l);
        firstEntity.addComponent(lightComponent);
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

}
