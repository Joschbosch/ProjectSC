/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.events.entity.movement.UpdatePositionEvent;
import de.projectsc.core.events.entity.movement.UpdateRotationEvent;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.terrain.Terrain;
import de.projectsc.modes.client.core.interfaces.ClientState;
import de.projectsc.modes.client.core.interfaces.GUI;
import de.projectsc.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.modes.client.gui.components.GraphicalComponentImplementation;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.input.InputConsumeManager;
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
import de.projectsc.modes.client.gui.states.GUIState;
import de.projectsc.modes.client.gui.states.GameGUIState;
import de.projectsc.modes.client.gui.states.MenuGUIState;
import de.projectsc.modes.client.gui.utils.MousePicker;

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

    private FontRenderer fontRenderer;

    private RenderingSystem renderingSystem;

    private ComponentManager componentManager;

    private EntityManager entityManager;

    private EventManager eventManager;

    private List<TerrainModel> terrainModels;

    private InputSystem inputSystem;

    private InputConsumeManager inputManager;

    private GUIState currentGUIState;

    private Timer timer;

    private String sun;

    public GUICore(ComponentManager componentManager, EntityManager entityManager, EventManager eventManager, Timer timer) {
        this.componentManager = componentManager;
        this.entityManager = entityManager;
        this.eventManager = eventManager;
        this.inputManager = new InputConsumeManager();
        this.timer = timer;
    }

    @Override
    public boolean init() {
        LOGGER.info("Initialize GUI core");
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle("Project SC");
            Display.setVSyncEnabled(true);
            Display.create();
        } catch (LWJGLException e) {
            LOGGER.error(e.getStackTrace());
        }

        LOGGER.info("Opened window ");
        LOGGER.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
        inputSystem = new InputSystem();
        LOGGER.info("Input system initialized.");
        LOGGER.info("Loading renderer ... ");
        masterRenderer = new MasterRenderer();
        LOGGER.info("Master ...");
        waterfbo = new WaterFrameBuffers();
        waterRenderer = new WaterRenderer(masterRenderer.getProjectionMatrix(), waterfbo);
        LOGGER.info("Water ...");
        uiRenderer = new UIRenderer();
        LOGGER.info("UI ...");
        LOGGER.info("Loading renderer done");
        camera = new Camera();
        inputManager.addListener(camera);
        LOGGER.info("Created camera.");
        mousePicker = new MousePicker(masterRenderer.getProjectionMatrix());
        LOGGER.info("Created mouse picker");
        TextMaster.init();
        fontRenderer = new FontRenderer();
        LOGGER.info("Initialized font rendering");
        loadGUIComponents();
        renderingSystem = new RenderingSystem(entityManager, eventManager);
        LOGGER.info("GUI components loaded.");
        running = true;

        // TEsting
        loadWorld();
        createSun();
        return running;
    }

    private void createSun() {
        sun = entityManager.createNewEntity();
        eventManager.fireEvent(new UpdateRotationEvent(sun, new Vector3f(0, 0, 0)));
        EmittingLightComponent lightComponent =
            (EmittingLightComponent) entityManager.addComponentToEntity(sun,
                GraphicalComponentImplementation.EMMITING_LIGHT_COMPONENT.getName());
        Transform position = entityManager.getEntity(sun).getTransform();
        Light light = new Light(new Vector3f(position.getPosition()), new Vector3f(1.0f, 1.0f, 1.0f), "sun");
        lightComponent.addLight(sun, new Vector3f(position.getPosition()), light);
        entityManager.addComponentToEntity(sun, ColliderComponent.NAME);
        eventManager.fireEvent(new UpdatePositionEvent(new Vector3f(0.0f, 100.0f, 100.0f), sun));

    }

    @Override
    public void loadWorld() {
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
    public void initState(ClientState newState) {
        if (currentGUIState != null) {
            currentGUIState.tearDown();
        }
        if (newState.getId().equals("Menu")) {
            currentGUIState = new MenuGUIState();
        }
        if (newState.getId().equals("Game")) {
            currentGUIState = new GameGUIState();
            ((GameGUIState) currentGUIState).setMousePicker(mousePicker);

        }

        currentGUIState.initialize();
        camera.setConsumeInput(currentGUIState.getCameraMoveable());
    }

    @Override
    public void render(Snapshot[] interpolationSnapshots, long interpolationTime) {
        render();
    }

    @Override
    public void render() {
        if (Display.isCloseRequested()) {
            LOGGER.info("Send close request and close down");
            running = false;
        }
        GUIText fps =
            TextMaster.createAndLoadText("FPS: " + timer.getCurrentFPS(), 0.7f, FontStore.getFont(Font.CANDARA),
                new Vector2f(0.0f, 0.0f), 5, false);
        if (!entityManager.getEntity(sun).getTransform().getPosition().equals(new Vector3f(0.0f, 100.0f, 100.0f))) {
            eventManager.fireEvent(new UpdatePositionEvent(new Vector3f(0.0f, 100.0f, 100.0f), sun));
        }

        camera.move(timer.getDelta());
        ParticleMaster.update(timer.getDelta(), camera.getPosition());
        renderingSystem.update(timer.getDelta());
        currentGUIState.update();
        if (currentGUIState.renderScene()) {
            mousePicker.update(getTerrains(), camera.getPosition(), camera.createViewMatrix());
            renderWater();
            GUIScene scene = renderingSystem.createScene();
            scene.setTerrains(terrainModels);
            scene.setDebugMode(currentGUIState.isDebugModeActive());
            masterRenderer.renderScene(scene, camera, timer.getDelta(), new Vector4f(0,
                1, 0, CLIPPING_PLANE_NOT_RENDERING));

        }
        UI ui = new UI();
        currentGUIState.getUIElements(ui);
        uiRenderer.render(ui.getUIElements(UI.BACKGROUND));
        fontRenderer.render(TextMaster.render(), UI.BACKGROUND);
        uiRenderer.render(ui.getUIElements(UI.FOREGROUND));
        fontRenderer.render(TextMaster.render(), UI.FOREGROUND);

        TextMaster.removeText(fps);
        Display.sync(MAX_FRAME_RATE);
        Display.update();
    }

    private List<Terrain> getTerrains() {
        List<Terrain> terrain = new LinkedList<>();
        if (terrainModels != null) {
            for (TerrainModel models : terrainModels) {
                terrain.add(models.getTerrain());
            }
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

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void readInput() {
        inputManager.processInput(inputSystem.updateInputs());
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
