/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.core.GUI;
import de.projectsc.client.core.data.Scene;
import de.projectsc.client.core.states.ClientState;
import de.projectsc.client.core.states.StateConstants;
import de.projectsc.client.core.ui.elements.Snapshot;
import de.projectsc.client.core.utils.Timer;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.render.MasterRenderer;
import de.projectsc.client.gui.render.UIRenderer;
import de.projectsc.client.gui.render.WaterRenderer;
import de.projectsc.client.gui.terrain.water.WaterFrameBuffers;
import de.projectsc.client.gui.text.TextMaster;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.MousePicker;

/**
 * Core class for the GUI.
 * 
 * @author Josch Bosch
 */
public class GUICore implements GUI {

    private static final int MAX_FRAME_RATE = 60;

    private static final int HEIGHT = 1024;

    private static final int WIDTH = 1280;

    private static final Log LOGGER = LogFactory.getLog(GUICore.class);

    private static final int CLIPPING_PLANE_NOT_RENDERING = 100000;

    private boolean running;

    private MasterRenderer masterRenderer;

    private WaterFrameBuffers waterfbo;

    private WaterRenderer waterRenderer;

    private Camera camera;

    private MousePicker mousePicker;

    private UIRenderer uiRenderer;

    public GUICore() {}

    @Override
    public boolean initCore() {
        LOGGER.debug("Initialize GUI core");
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle("Project SC");
            Display.setVSyncEnabled(true);
            Display.create();
        } catch (LWJGLException e) {
            LOGGER.error(e.getStackTrace());
        }
        LOGGER.debug("Opened window ");
        LOGGER.debug("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
        LOGGER.debug("Loading renderer ... ");
        masterRenderer = new MasterRenderer();
        LOGGER.debug("Master ...");
        waterfbo = new WaterFrameBuffers();
        waterRenderer = new WaterRenderer(masterRenderer.getProjectionMatrix(), waterfbo);
        LOGGER.debug("Water ...");
        uiRenderer = new UIRenderer();
        LOGGER.debug("UI ...");
        LOGGER.debug("Loading renderer done.");
        camera = new Camera();
        mousePicker = new MousePicker(camera, masterRenderer.getProjectionMatrix());
        LOGGER.debug("Initialize font rendering.");
        TextMaster.init();
        running = true;
        return running;
    }

    @Override
    public boolean initState(ClientState state) {
        LOGGER.debug("Initialize state " + state.getClass());
        load(state.getGUIObjectsToLoad());
        return true;
    }

    private void load(Map<String, List<String>> objectsToLoad) {
        for (String image : objectsToLoad.get(StateConstants.IMAGES)) {
            Loader.loadTexture(image);
        }
    }

    @Override
    public void render(ClientState state, Snapshot snapshot) {
        if (Display.isCloseRequested()) {
            LOGGER.debug("Send close request and close down");
            running = false;
        }
        if (state != null) {
            camera.move(Timer.getDelta());

            // if (waters != null && waters.size() > 0) {
            // waterfbo.bindReflectionFrameBuffer();
            //
            // float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
            // camera.getPosition().y -= distance;
            // camera.invertPitch();
            // masterRenderer.renderScene(terrain.getModel(), (List<Entity>) staticEntities.values(), camera, elapsedTime,
            // new Vector4f(0, 1, 0, -waters
            // .get(0).getHeight()));
            // camera.getPosition().y += distance;
            // camera.invertPitch();
            //
            // waterfbo.bindRefractionFrameBuffer();
            // masterRenderer.renderScene(terrain.getModel(), (List<Entity>) staticEntities.values(), camera, elapsedTime,
            // new Vector4f(0, 0 - 1, 0, waters
            // .get(0).getHeight()));
            //
            // waterfbo.unbindCurrentFrameBuffer();
            // }
            // mousePicker.update();
            Scene scene = state.getScene();
            masterRenderer.renderScene(scene, camera, Timer.getDelta(), new Vector4f(0,
                1, 0, CLIPPING_PLANE_NOT_RENDERING));
            // if (waters != null && waters.size() > 0) {
            // waterRenderer.render(waters, camera, elapsedTime);
            // }
            uiRenderer.render(state.getUI());
            TextMaster.render();
        }
        Display.sync(MAX_FRAME_RATE);
        Display.update();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Map<Integer, Integer> readInput() {
        Map<Integer, Integer> keyMap = new HashMap<>();
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
                if (Keyboard.getEventKeyState()) {
                    keyMap.put(Keyboard.KEY_DOWN, 1);
                } else {
                    keyMap.put(Keyboard.KEY_DOWN, 2);
                }
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
                if (Keyboard.getEventKeyState()) {
                    keyMap.put(Keyboard.KEY_UP, 1);
                } else {
                    keyMap.put(Keyboard.KEY_UP, 2);
                }
            }
        }
        return keyMap;
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleInput() {
        // TODO Auto-generated method stub

    }

    @Override
    public void terminate() {
        // TODO Auto-generated method stub

    }

}
