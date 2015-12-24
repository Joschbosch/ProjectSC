/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.data.InputCommand;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.manager.InputConsumeManager;
import de.projectsc.core.terrain.Terrain;
import de.projectsc.modes.client.core.states.GameState;
import de.projectsc.modes.client.gui.components.GraphicalComponentImplementation;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.data.View;
import de.projectsc.modes.client.gui.input.InputSystem;
import de.projectsc.modes.client.gui.objects.Camera;
import de.projectsc.modes.client.gui.objects.particles.ParticleMaster;
import de.projectsc.modes.client.gui.objects.terrain.TerrainModel;
import de.projectsc.modes.client.gui.objects.terrain.water.WaterFrameBuffers;
import de.projectsc.modes.client.gui.objects.text.Font;
import de.projectsc.modes.client.gui.objects.text.FontStore;
import de.projectsc.modes.client.gui.objects.text.GUIText;
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

    private ComponentManager componentManager;

    private EntityManager entityManager;

    private EventManager eventManager;

    private List<TerrainModel> terrainModels;

    private InputSystem inputSystem;

    private InputConsumeManager inputManager;

    public GUICore(ComponentManager componentManager, EntityManager entityManager, EventManager eventManager,
        InputConsumeManager inputManager) {
        this.componentManager = componentManager;
        this.entityManager = entityManager;
        this.eventManager = eventManager;
        this.inputManager = inputManager;
    }

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
        inputSystem = new InputSystem();
        LOGGER.debug("Input system initialized.");
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
        inputManager.addListener(camera);
        LOGGER.debug("Created camera.");
        mousePicker = new MousePicker(masterRenderer.getProjectionMatrix());
        LOGGER.debug("Created mouse picker");
        TextMaster.init();
        fontRenderer = new FontRenderer();
        LOGGER.debug("Initialized font rendering");
        loadGUIComponents();
        renderingSystem = new RenderingSystem(entityManager, eventManager);
        LOGGER.debug("GUI components loaded.");
        running = true;
        return running;
    }

    @Override
    public void loadTerrain() {
        int k = 10;
        int l = 10;
        String texture = "terrain/grass.png";
        terrainModels = new LinkedList<>();
        for (int i = -k; i < k; i++) {
            for (int j = -l; j < l; j++) {
                Terrain terrain =
                    new Terrain(i, j, texture, texture, texture, texture);
                TerrainModel model = new TerrainModel(terrain);
                terrainModels.add(model);
            }
        }

        mousePicker = new MousePicker(masterRenderer.getProjectionMatrix());
    }

    private void loadGUIComponents() {
        for (GraphicalComponentImplementation it : GraphicalComponentImplementation.values()) {
            componentManager.registerComponent(it.getName(), it.getClazz());
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
        TextMaster.removeAll();
    }

    @Override
    public void render(ClientState state) {
        if (Display.isCloseRequested()) {
            LOGGER.debug("Send close request and close down");
            running = false;
        }
        GUIText fps =
            TextMaster.createAndLoadText("FPS: " + Timer.getCurrentFPS(), 0.7f, FontStore.getFont(Font.CANDARA),
                new Vector2f(0.0f, 0.0f), 5, false);
        if (state != null) {
            camera.move(Timer.getDelta());
            ParticleMaster.update(camera.getPosition());
            renderingSystem.update(Timer.getDelta());
            if (state instanceof GameState) {
                mousePicker.update(getTerrains(), camera.getPosition(), camera.createViewMatrix());
                if (state.getSnapshot() != null) {
                    renderWater();
                    GUIScene scene = renderingSystem.createScene();
                    scene.setTerrains(terrainModels);
                    scene.setDebugMode(false);
                    masterRenderer.renderScene(scene, camera, Timer.getDelta(), new Vector4f(0,
                        1, 0, CLIPPING_PLANE_NOT_RENDERING));
                }
            }
            UI ui = createUI();
            uiRenderer.render(ui.getUIElements(UI.BEFORE_TEXT));
            fontRenderer.render(TextMaster.render(), 0);
            uiRenderer.render(ui.getUIElements(UI.AFTER_TEXT));
            fontRenderer.render(TextMaster.render(), 1);
        }
        TextMaster.removeText(fps);
        Display.sync(MAX_FRAME_RATE);
        Display.update();
    }

    private List<Terrain> getTerrains() {
        List<Terrain> terrain = new LinkedList<>();
        for (TerrainModel models : terrainModels) {
            terrain.add(models.getTerrain());
        }
        return terrain;
    }

    private void renderWater() {
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
        // if (waters != null && waters.size() > 0) {
        // waterRenderer.render(waters, camera, elapsedTime);
        // }
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
    public Queue<InputCommand> readInput() {
        return inputSystem.updateInputs();
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
        TextMaster.cleanUp();
        ParticleMaster.dispose();
    }

}
