/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.editor;

import java.awt.Canvas;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.EntityEditor;
import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.impl.physic.ColliderComponent;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.physics.WireFrame;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.events.entities.ChangeEntitySelectEvent;
import de.projectsc.core.events.movement.ChangePositionEvent;
import de.projectsc.core.events.movement.ChangeRotationEvent;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.manager.InputConsumeManager;
import de.projectsc.core.systems.physics.PhysicsSystem;
import de.projectsc.core.terrain.Terrain;
import de.projectsc.modes.client.gui.InputSystem;
import de.projectsc.modes.client.gui.RenderingSystem;
import de.projectsc.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.modes.client.gui.components.GraphicalComponentImplementation;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.objects.Camera;
import de.projectsc.modes.client.gui.objects.particles.ParticleMaster;
import de.projectsc.modes.client.gui.objects.terrain.TerrainModel;
import de.projectsc.modes.client.gui.objects.text.Font;
import de.projectsc.modes.client.gui.objects.text.FontStore;
import de.projectsc.modes.client.gui.objects.text.GUIText;
import de.projectsc.modes.client.gui.objects.text.TextMaster;
import de.projectsc.modes.client.gui.render.FontRenderer;
import de.projectsc.modes.client.gui.render.MasterRenderer;
import de.projectsc.modes.client.gui.utils.MousePicker;

/**
 * Core class for the GUI.
 * 
 * @author Josch Bosch
 */
public class MapEditorGraphicsCore implements Runnable {

    private static final String SLASHED_MODEL_DIR = CoreConstants.SCHEME_DIRECTORY_NAME + "/";

    private static final String SEPARATOR = ";";

    private static final Log LOGGER = LogFactory.getLog(MapEditorGraphicsCore.class);

    private boolean running;

    private final Canvas displayParent;

    private final int width;

    private final int height;

    private Camera camera;

    private MousePicker mousePicker;

    private MasterRenderer masterRenderer;

    private final BlockingQueue<String> incomingQueue;

    private String sun;

    private final AtomicBoolean doRender = new AtomicBoolean(true);

    private final Map<Long, EntitySchema> entitySchemas = new TreeMap<>();

    private String entityAtCursor = "";

    private boolean alreadyClicked;

    private int mode;

    private String selectedEntity = "";

    private List<TerrainModel> terrainModels;

    private RenderingSystem renderSystem;

    private PhysicsSystem physicsSystem;

    private ComponentManager componentManager;

    private FontRenderer fontRenderer;

    private EntityManager entityManager;

    private EventManager eventManager;

    private InputConsumeManager inputConsumeManager;

    private InputSystem inputSystem;

    public MapEditorGraphicsCore(Canvas displayParent, int width, int height, BlockingQueue<String> messageQueue,
        ComponentManager componentManager, EntityManager entityManager, EventManager eventManager, InputConsumeManager inputConsumeManager) {
        incomingQueue = new LinkedBlockingQueue<>();
        this.displayParent = displayParent;
        this.width = width;
        this.height = height;
        this.componentManager = componentManager;
        this.entityManager = entityManager;
        this.eventManager = eventManager;
        this.inputConsumeManager = inputConsumeManager;
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
        } catch (LWJGLException e) {
        }
        loadGUIComponents();
        physicsSystem = new PhysicsSystem(entityManager, eventManager);
        renderSystem = new RenderingSystem(entityManager, eventManager);
        inputSystem = new InputSystem();

        TextMaster.init();
        camera = new Camera();
        inputConsumeManager.addListener(camera);
        camera.setLookAtPoint(0, 0, 0);
        masterRenderer = new MasterRenderer();
        fontRenderer = new FontRenderer();
        createSun();
        createTerrain(10, 10, "");
        loadEntitySchemas();
        newMouseEntity(10000L);
        gameLoop();
    }

    private void loadGUIComponents() {
        for (GraphicalComponentImplementation it : GraphicalComponentImplementation.values()) {
            componentManager.registerComponent(it.getName(), it.getClazz());
        }
    }

    private void loadEntitySchemas() {

        try {
            File folder = new File(MapEditorGraphicsCore.class.getResource(SLASHED_MODEL_DIR).toURI());
            for (File schemaDir : folder.listFiles()) {
                try {
                    if (schemaDir.getName().matches(CoreConstants.SCHEME_DIRECTORY_PREFIX + "\\d{5}")) {
                        EntitySchema newSchema = new EntitySchema(Integer.parseInt(schemaDir.getName().substring(1)));
                        ObjectMapper mapper = new ObjectMapper();
                        File entityFile = new File(schemaDir, CoreConstants.ENTITY_FILENAME);
                        JsonNode tree = mapper.readTree(entityFile);
                        Iterator<String> componentNamesIterator = tree.get("components").getFieldNames();
                        while (componentNamesIterator.hasNext()) {
                            String name = componentNamesIterator.next();
                            java.util.Map<String, Object> serialized =
                                mapper.readValue(tree.get("components").get(name), new HashMap<String, Object>().getClass());
                            Component c = componentManager.createComponent(name);
                            if (c != null) {
                                c.deserialize(serialized, schemaDir);
                                newSchema.components.add(c);
                            }
                        }
                        entitySchemas.put(newSchema.getId(), newSchema);
                    }
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
        } catch (URISyntaxException e1) {
            LOGGER.error(e1);
        }

    }

    protected void gameLoop() {
        Timer.init();
        while (running) {
            readMessages();
            Timer.update();
            inputConsumeManager.processInput(inputSystem.updateInputs());
            camera.move(Timer.getDelta());
            ParticleMaster.update(camera.getPosition());
            if (entityManager.getEntity(sun) != null && !entityManager.getEntity(sun).getTransform().getPosition().equals(
                camera.getPosition())) {
                eventManager.fireEvent(new ChangePositionEvent(camera.getPosition(), sun));
            }

            physicsSystem.update(Timer.getDelta());
            renderSystem.update(Timer.getDelta());

            GUIText fps =
                TextMaster.createAndLoadText("FPS: " + Timer.getCurrentFPS(), 0.7f, FontStore.getFont(Font.CANDARA),
                    new Vector2f(0.0f, 0.0f), 5, false);
            if (terrainModels != null) {
                mousePicker.update(getTerrains(), camera.getPosition(), camera.createViewMatrix());
                readInput();

                if (camera != null && mousePicker.getCurrentRay() != null) {
                    Entity highlightingEntity = null;
                    float tMin = Float.MAX_VALUE;
                }
                if (!entityAtCursor.isEmpty() && mousePicker.getCurrentTerrainPoint() != null) {
                    entityManager.getEntity(entityAtCursor).getTransform().setPosition(mousePicker.getCurrentTerrainPoint());
                }

                if (doRender.get()) {
                    GUIScene s = renderSystem.createScene();
                    physicsSystem.debug(s);
                    s.setTerrains(terrainModels);
                    s.setRenderSkybox(true);
                    if (!entityAtCursor.isEmpty()) {
                        s.getWireFrames().add(
                            new WireFrame(WireFrame.SPHERE, entityManager.getEntity(entityAtCursor).getTransform().getPosition(),
                                new Vector3f(), new Vector3f(0.5f, 0.5f,
                                    0.5f)));
                    }
                    masterRenderer.renderScene(s, camera, Timer.getDelta(), new Vector4f(0, 100000000, 0, 100000000));

                    fontRenderer.render(TextMaster.render(), 0);
                }
            }
            TextMaster.removeText(fps);
            Display.sync(60);
            Display.update();
        }
        Display.destroy();
    }

    private List<Terrain> getTerrains() {
        List<Terrain> terrain = new LinkedList<>();
        for (TerrainModel models : terrainModels) {
            terrain.add(models.getTerrain());
        }
        return terrain;
    }

    private void readInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            entityManager.getEntity(entityAtCursor).getTransform().setScale(
                Vector3f.add(entityManager.getEntity(entityAtCursor).getTransform().getScale(), new Vector3f(0.5f, 0.5f, 0.5f), null));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
            if (entityManager.getEntity(entityAtCursor).getTransform().getScale().x > 0.5
                && entityManager.getEntity(entityAtCursor).getTransform().getScale().y > 0.5
                && entityManager.getEntity(entityAtCursor).getTransform().getScale().z > 0.5) {
                entityManager.getEntity(entityAtCursor).getTransform()
                    .setScale(Vector3f.sub(entityManager.getEntity(entityAtCursor).getTransform().getScale(), new Vector3f(0.5f,
                        0.5f, 0.5f), null));
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            entityManager.getEntity(entityAtCursor).getTransform().setRotation(
                Vector3f.sub(entityManager.getEntity(entityAtCursor).getTransform().getRotation(), new Vector3f(0, 0.5f, 0), null));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            entityManager.getEntity(entityAtCursor).getTransform().setRotation(
                Vector3f.add(entityManager.getEntity(entityAtCursor).getTransform().getRotation(), new Vector3f(0, 0.5f, 0), null));
        }
        if (Mouse.isButtonDown(0) && mode == 0 && !alreadyClicked) {
            alreadyClicked = true;
            newMouseEntity(entityManager.getEntity(entityAtCursor).getEntityTypeId());
        }
        if (Mouse.isButtonDown(0) && mode == 1 && !alreadyClicked) {
            alreadyClicked = true;
            for (String e : entityManager.getAllEntites()) {
                if (entityManager.hasComponent(e, ColliderComponent.class)) {
                    ColliderComponent collider = (ColliderComponent) entityManager.getComponent(e, ColliderComponent.class);
                    if (collider.intersects(entityManager.getEntity(e).getTransform(), camera.getPosition(), mousePicker.getCurrentRay()) > 0) {
                        if (!selectedEntity.isEmpty()) {
                            eventManager.fireEvent(new ChangeEntitySelectEvent(selectedEntity, false, false));
                        }
                        selectedEntity = e;
                        eventManager.fireEvent(new ChangeEntitySelectEvent(e, true, false));

                    }
                }
            }
        }

        if (!Mouse.isButtonDown(0)) {
            alreadyClicked = false;
        }
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
                if (Keyboard.getEventKeyState()) {
                    long id = entityManager.getEntity(entityAtCursor).getEntityTypeId();
                    id = (id + 1) % 1000000;
                    while (entitySchemas.get(id) == null) {
                        id = (id + 1) % 1000000;
                    }
                    String oldEntity = entityAtCursor;
                    newMouseEntity(id);
                    entityManager.deleteEntity(oldEntity);
                }
            }
        }

    }

    private void newMouseEntity(long type) {
        String e = entityManager.createNewEntity();
        if (entityAtCursor.isEmpty()) {
            entitySchemas.get(type).createNewEntity(entityManager.getEntity(e).getTransform(), e, entityManager);
        } else {
            entitySchemas.get(type).createNewEntity(entityManager.getEntity(entityAtCursor).getTransform(), e, entityManager);
        }
        entityAtCursor = e;

    }

    private void createSun() {
        sun = entityManager.createNewEntity();
        eventManager.fireEvent(new ChangePositionEvent(new Vector3f(0.0f, 100.0f, 100.0f), sun));
        eventManager.fireEvent(new ChangeRotationEvent(new Vector3f(0, 0, 0), sun));
        EmittingLightComponent lightComponent =
            (EmittingLightComponent) entityManager.addComponentToEntity(sun,
                GraphicalComponentImplementation.EMMITING_LIGHT_COMPONENT.getName());
        Transform position = entityManager.getEntity(sun).getTransform();
        Light light = new Light(new Vector3f(position.getPosition()), new Vector3f(1.0f, 1.0f, 1.0f), "sun");
        lightComponent.addLight(sun, new Vector3f(position.getPosition()), light);
        entityManager.addComponentToEntity(sun, ColliderComponent.NAME);
    }

    private void createTerrain(int k, int l, String parsed) {
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

    private void readMessages() {
        while (!incomingQueue.isEmpty()) {
            String msg = incomingQueue.poll();
            if (msg.startsWith("createTerrain")) {
                String[] parsed = msg.split(SEPARATOR);
                createTerrain(Integer.parseInt(parsed[1]), Integer.parseInt(parsed[2]), parsed[3]);
            }
        }
    }

    /**
     * Stop.
     */
    public void stop() {
        running = false;
    }

    /**
     * @param value true, if rendering is enabled.
     */
    public void doRender(boolean value) {
        doRender.set(value);
    }

    /**
     * @param terrainWidth width
     * @param terrainHeight height
     * @param name of terrain texture.
     */
    public void triggerCreateTerrain(int terrainWidth, int terrainHeight, String name) {
        incomingQueue.offer("createTerrain;" + terrainWidth + SEPARATOR + terrainWidth + SEPARATOR + name);
    }

    /**
     * @param mode selection or adding mode
     */
    public void setMode(String mode) {
        if (mode.equals("add")) {
            if (this.mode != 0) {
                this.mode = 0;
                newMouseEntity(10000L);
            }

        } else {
            if (this.mode != 1) {
                this.mode = 1;
                selectedEntity = "";
                entityManager.deleteEntity(entityAtCursor);
                entityAtCursor = "";
            }

        }
    }

    public void performSave() {
        File levelRootDirectory = null;
        try {
            levelRootDirectory = new File(EntityEditor.class.getResource("/level/").toURI());

            File levelFolder =
                new File(levelRootDirectory, CoreConstants.LEVEL_DIRECTORY_PREFIX + 1);
            levelFolder.mkdir();
            Map<String, Object> complete = new HashMap<>();
            Map<String, Object> entities = new HashMap<>();
            for (String e : entityManager.getAllEntites()) {
                Map<String, Object> props = new HashMap<>();
                props.put("type", entityManager.getEntity(e).getEntityTypeId());
                props.put("transform", entityManager.getEntity(e).getTransform());
                entities.put(e, props);
            }
            complete.put("Terrain", new HashMap<String, Object>());
            complete.put("Entities", entities);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(levelFolder, "first.map"), complete);
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(e);
        }
    }
}
