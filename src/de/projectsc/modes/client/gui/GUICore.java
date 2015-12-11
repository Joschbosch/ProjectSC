/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.modes.client.gui.components.GraphicalComponentImplementation;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.data.View;
import de.projectsc.modes.client.gui.objects.Camera;
import de.projectsc.modes.client.gui.objects.terrain.water.WaterFrameBuffers;
import de.projectsc.modes.client.gui.objects.text.TextMaster;
import de.projectsc.modes.client.gui.render.FontRenderer;
import de.projectsc.modes.client.gui.render.MasterRenderer;
import de.projectsc.modes.client.gui.render.UIRenderer;
import de.projectsc.modes.client.gui.render.WaterRenderer;
import de.projectsc.modes.client.gui.ui.views.UIFactory;
import de.projectsc.modes.client.gui.utils.MousePicker;
import de.projectsc.modes.client.interfaces.ClientState;
import de.projectsc.modes.client.interfaces.GUI;
import de.projectsc.modes.client.ui.BasicUIElement;

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

    @SuppressWarnings("unused")

    private MousePicker mousePicker;

    private UIRenderer uiRenderer;

    private final Map<BasicUIElement, View> registeredViews = new HashMap<>();

    private FontRenderer fontRenderer;

    private RenderingSystem renderingSystem;

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
        LOGGER.debug("Loading renderer done");
        camera = new Camera();
        LOGGER.debug("Created camera.");
        mousePicker = new MousePicker(masterRenderer.getProjectionMatrix());
        LOGGER.debug("Created mouse picker");
        TextMaster.init();
        fontRenderer = new FontRenderer();
        LOGGER.debug("Initialized font rendering");
        loadGUIComponents();
        renderingSystem = new RenderingSystem();
        LOGGER.debug("GUI components loaded.");
        running = true;
        return running;
    }

    private void loadGUIComponents() {
        for (GraphicalComponentImplementation it : GraphicalComponentImplementation.values()) {
            ComponentManager.registerComponent(it.getName(), it.getClazz());
        }
    }

    @Override
    public void registerView(BasicUIElement element) {
        View view = UIFactory.createView(element);
        if (view != null) {
            registeredViews.put(element, view);
        }
    }

    @Override
    public void unregisterView(BasicUIElement element) {
        registeredViews.remove(element);
    }

    @Override
    public boolean initState(ClientState state) {
        LOGGER.debug("Initialize state " + state.getClass());
        for (BasicUIElement element : state.getUI()) {
            registerView(element);
        }
        return true;
    }

    @Override
    public void cleanUpState(ClientState state) {
        LOGGER.debug("Clean up state " + state.getClass());
        for (BasicUIElement element : state.getUI()) {
            unregisterView(element);
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
            // masterRenderer.renderScene(terrain.getModel(), (List<Entity>)
            // staticEntities.values(), camera, elapsedTime,
            // new Vector4f(0, 1, 0, -waters
            // .get(0).getHeight()));
            // camera.getPosition().y += distance;
            // camera.invertPitch();
            //
            // waterfbo.bindRefractionFrameBuffer();
            // masterRenderer.renderScene(terrain.getModel(), (List<Entity>)
            // staticEntities.values(), camera, elapsedTime,
            // new Vector4f(0, 0 - 1, 0, waters
            // .get(0).getHeight()));
            //
            // waterfbo.unbindCurrentFrameBuffer();
            // }
            // mousePicker.update(null, null, null);
            GUIScene scene = renderingSystem.createScene();
            masterRenderer.renderScene(scene, camera, Timer.getDelta(), new Vector4f(0,
                1, 0, CLIPPING_PLANE_NOT_RENDERING));
            // if (waters != null && waters.size() > 0) {
            // waterRenderer.render(waters, camera, elapsedTime);
            // }
            UI ui = createUI();
            uiRenderer.render(ui.getUIElements(UI.BEFORE_TEXT));
            fontRenderer.render(TextMaster.render(), 0);
            uiRenderer.render(ui.getUIElements(UI.AFTER_TEXT));
            fontRenderer.render(TextMaster.render(), 1);
        }
        Display.sync(MAX_FRAME_RATE);
        Display.update();
    }

    private UI createUI() {
        UI ui = new UI();
        for (View v : registeredViews.values()) {
            v.render(ui);
        }
        return ui;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Map<Integer, Integer> readInput() {
        Map<Integer, Integer> keyMap = new HashMap<>();
        while (Keyboard.next()) {
            for (int i = 0; i < Keyboard.getKeyCount(); i++) {
                if (i == Keyboard.getEventKey() && Keyboard.getEventKeyState()) {
                    keyMap.put(i, 1);
                } else {
                    keyMap.put(i, 2);
                }
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_DOWN && Keyboard.getEventKeyState()) {
                keyMap.put(Keyboard.KEY_DOWN, 1);
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_UP && Keyboard.getEventKeyState()) {
                keyMap.put(Keyboard.KEY_UP, 1);
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
    public void terminate() {}

    @Override
    public void cleanUpCore() {
        masterRenderer.dispose();
        uiRenderer.dispose();
        waterRenderer.dispose();
        waterfbo.dispose();
    }

}
