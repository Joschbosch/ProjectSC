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
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.content.Map;
import de.projectsc.core.data.messages.GUIMessage;
import de.projectsc.gui.Camera;
import de.projectsc.gui.content.GUICommand;
import de.projectsc.gui.content.MiniMap;
import de.projectsc.gui.entities.Entity;
import de.projectsc.gui.models.RawModel;
import de.projectsc.gui.models.TexturedModel;
import de.projectsc.gui.render.Light;
import de.projectsc.gui.render.Loader;
import de.projectsc.gui.render.ModelLoader;
import de.projectsc.gui.render.Renderer;
import de.projectsc.gui.shaders.StaticShader;
import de.projectsc.gui.textures.ModelTexture;

/**
 * 
 * State when the game is running (drawing the map etc.).
 * 
 * @author Josch Bosch
 */
public class StateGameRunning implements State {

    private static final Log LOGGER = LogFactory.getLog(StateGameRunning.class);

    private static final GUIState STATE = GUIState.GAME;

    private static final float FOV = 90f;

    private static final float NEAR_PLANE = 0.1f;

    private static final float FAR_PLANE = 1000f;

    private Map currentMap;

    @SuppressWarnings("unused")
    private final BlockingQueue<GUIMessage> outgoingQueue;

    private MiniMap minimap;

    private final BlockingQueue<GUICommand> drawableQueue = new LinkedBlockingQueue<>();

    private final Camera camera;

    private StaticShader staticShader;

    private final Loader loader;

    private TexturedModel texturedModel;

    private Entity entity;

    private Matrix4f projectionMatrix;

    private Light light;

    public StateGameRunning(BlockingQueue<GUIMessage> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
        camera = new Camera();
        loader = new Loader();
    }

    @Override
    public void initialize() {
        loadShader();
        LOGGER.debug("Loading models and light ... ");
        RawModel model = ModelLoader.loadModel("stall.obj", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("DungeonCrawl_ProjectUtumnoTileset.png"));
        texturedModel = new TexturedModel(model, texture);
        entity = new Entity(texturedModel, new Vector3f(0 - 1, 0, 0), 0, 0, 0, 1);
        light = new Light(new Vector3f(0, 0, 0 - 2 * 10), new Vector3f(1, 1, 1));
        LOGGER.debug("Models loaded");
    }

    private void loadShader() {
        LOGGER.debug("Loading static shader ...");
        staticShader = new StaticShader();
        createProjectionMatrix();
        staticShader.start();
        staticShader.loadProjectionMatrix(projectionMatrix);
        staticShader.stop();
        LOGGER.debug("Static shader loaded.");
    }

    @Override
    public void pause() {
        LOGGER.debug("Pause state" + STATE.name());
    }

    @Override
    public void resume() {
        LOGGER.debug("Resume state" + STATE.name());
    }

    private void newRendering() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        Renderer.renderModel(entity, staticShader);
    }

    @Override
    public void render(long elapsedTime) {
        update();
        camera.move();
        staticShader.start();
        staticShader.loadViewMatrix(camera);
        staticShader.loadLight(light);
        staticShader.stop();
        newRendering();
    }

    @Override
    public void update() {

    }

    @Override
    public void terminate() {
        LOGGER.debug("Terminate state " + STATE.name());
        loader.dispose();
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
}
