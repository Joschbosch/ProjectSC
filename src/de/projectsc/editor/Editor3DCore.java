/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.editor;

import java.awt.Canvas;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.GraphicalEntity;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.render.MasterRenderer;
import de.projectsc.client.gui.terrain.TerrainModel;
import de.projectsc.client.gui.textures.TerrainTexture;
import de.projectsc.client.gui.textures.TerrainTexturePack;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.MousePicker;
import de.projectsc.core.Terrain;
import de.projectsc.core.TerrainLoader;

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

    private MasterRenderer masterRenderer;

    private Camera camera;

    private MousePicker mousePicker;

    private TerrainModel terrainModel;

    private List<Light> lights;

    private Object staticEntities;

    private final BlockingQueue<String> messageQueue;

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
            Display.setTitle("Project SC Editor");
            Display.setVSyncEnabled(true);
            Display.setParent(displayParent);
            Display.create();
            initGL();
        } catch (LWJGLException e) {
        }
        loader = new Loader();
        masterRenderer = new MasterRenderer(loader);
        camera = new Camera(null);
        mousePicker = new MousePicker(camera, masterRenderer.getProjectionMatrix(), terrainModel);
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

            if (terrainModel != null) {
                camera.move(delta);
                mousePicker.update();
                masterRenderer.renderScene(terrainModel, new LinkedList<GraphicalEntity>(), new LinkedList<GraphicalEntity>(), lights,
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
        loadTerrain("map");
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
        lights = terrain.getStaticLights();
        // if (staticEntities == null) {
        // staticEntities = new TreeMap<>();
        // }
        // staticEntities.putAll(terrain.getStaticObjects());
    }

}
