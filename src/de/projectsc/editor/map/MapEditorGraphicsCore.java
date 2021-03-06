/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.editor.map;

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

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.entities.EntitySchema;
import de.projectsc.core.events.entity.movement.UpdatePositionEvent;
import de.projectsc.core.events.entity.movement.UpdateRotationEvent;
import de.projectsc.core.events.entity.state.UpdateEntitySelectionEvent;
import de.projectsc.core.events.input.MousePositionChangedAction;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.physics.collision.CollisionSystem;
import de.projectsc.core.systems.state.EntityStateSystem;
import de.projectsc.core.terrain.Terrain;
import de.projectsc.core.utils.Maths;
import de.projectsc.editor.entity.EntityEditor;
import de.projectsc.modes.client.gui.InputSystem;
import de.projectsc.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.modes.client.gui.components.GraphicalComponentImplementation;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.input.InputConsumeManager;
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

    private EditorMouseEntity mouseEntity;

    private boolean alreadyClicked;

    private int mode;

    private String selectedEntity = "";

    private List<TerrainModel> terrainModels;

    private MapEditorRenderingSystem renderSystem;

    private ComponentManager componentManager;

    private FontRenderer fontRenderer;

    private EntityManager entityManager;

    private EventManager eventManager;

    private InputConsumeManager inputConsumeManager;

    private InputSystem inputSystem;

    private Timer timer;

    private CollisionSystem collisionSystem;

    private boolean rightDown = false;

    private EntityStateSystem entityStateSystem;

    public MapEditorGraphicsCore(MapEditor mapEditor, Canvas displayParent, int width, int height, BlockingQueue<String> messageQueue,
        ComponentManager componentManager, EntityManager entityManager, EventManager eventManager) {
        incomingQueue = new LinkedBlockingQueue<>();
        this.displayParent = displayParent;
        this.width = width;
        this.height = height;
        this.componentManager = componentManager;
        this.entityManager = entityManager;
        this.eventManager = eventManager;
        this.inputConsumeManager = new InputConsumeManager();
        this.timer = new Timer();
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
        mouseEntity = new EditorMouseEntity();
        entityStateSystem = new EntityStateSystem(entityManager, eventManager);
        collisionSystem = new CollisionSystem(entityManager, eventManager);
        renderSystem = new MapEditorRenderingSystem(entityManager, eventManager);
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
            String e = entityManager.createNewEntity();
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
                            @SuppressWarnings("unchecked") java.util.Map<String, Object> serialized =
                                mapper.readValue(tree.get("components").get(name), new HashMap<String, Object>().getClass());
                            Component c = componentManager.createComponent(name);
                            c.setOwner(entityManager.getEntity(e));
                            if (c != null) {
                                c.deserialize(serialized, schemaDir.getAbsolutePath());
                                newSchema.getComponents().add(c);
                            }
                        }
                        entitySchemas.put(newSchema.getId(), newSchema);
                    }
                } catch (IOException e1) {
                    LOGGER.error(e1);
                }
            }
        } catch (URISyntaxException e1) {
            LOGGER.error(e1);
        }

    }

    protected void gameLoop() {
        timer.init();
        while (running) {
            readMessages();
            timer.update();
            inputConsumeManager.processInput(inputSystem.updateInputs());
            camera.move(timer.getDelta());
            ParticleMaster.update(timer.getDelta(), camera.getPosition());
            if (entityManager.getEntity(sun) != null
                && !((TransformComponent) entityManager.getComponent(sun, TransformComponent.class)).getTransform().getPosition().equals(
                    camera.getPosition())) {
                eventManager.fireEvent(new UpdatePositionEvent(camera.getPosition(), sun));
            }

            // physicsSystem.update(timer.getDelta());
            collisionSystem.update(timer.getDelta());
            renderSystem.update(timer.getDelta());
            entityStateSystem.update(timer.getDelta());
            GUIText fps =
                TextMaster.createAndLoadText("FPS: " + timer.getCurrentFPS(), 0.7f, FontStore.getFont(Font.CANDARA),
                    new Vector2f(0.0f, 0.0f), 5, false);
            if (terrainModels != null) {
                mousePicker.update(getTerrains(), camera.getPosition(), camera.getViewMatrix());
                readInput();
                if (mouseEntity != null) {
                    mouseEntity.getT().setPosition(mousePicker.getCurrentTerrainPoint());
                }
                if (doRender.get()) {
                    GUIScene s = renderSystem.createScene(collisionSystem.getOctree(), mouseEntity,
                        entitySchemas.get(mouseEntity.getType()).getComponents());
                    s.setTerrains(terrainModels);
                    s.setRenderSkybox(true);
                    masterRenderer.renderScene(s, camera, timer.getDelta(), new Vector4f(0, 100000000, 0, 100000000));
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
            mouseEntity.getT().getScale().x += 0.1f;
            mouseEntity.getT().getScale().y += 0.1f;
            mouseEntity.getT().getScale().z += 0.1f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
            mouseEntity.getT().getScale().x -= 0.1f;
            mouseEntity.getT().getScale().y -= 0.1f;
            mouseEntity.getT().getScale().z -= 0.1f;

        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            mouseEntity.getT().getRotation().y -= 1.1f;

        }
        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            mouseEntity.getT().getRotation().y += 1.1f;

        }
        if (Mouse.isButtonDown(0) && mode == 0 && !alreadyClicked) {
            alreadyClicked = true;
            newMouseEntity(mouseEntity.getType());
        } 
        if (Mouse.isButtonDown(1) && mode == 0) {
            for (String entity : entityManager.getAllEntites()){
                if (entityManager.hasComponent(entity, ColliderComponent.class)){
                    entityManager.removeComponentFromEntity(entity, ColliderComponent.NAME);
                }
            }
            
        } 
        eventManager.fireEvent(new MousePositionChangedAction(mousePicker.getCurrentRay(), mousePicker.getCurrentCameraPosition()));
        calculateSelectionAndHighlighting();
        if (!Mouse.isButtonDown(0)) {
            alreadyClicked = false;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            if (Keyboard.getEventKeyState()) {
                if (!rightDown) {
                    rightDown = true;
                    long id = mouseEntity.getType();
                    id = (id + 1) % 1000000;
                    while (entitySchemas.get(id) == null) {
                        id = (id + 1) % 1000000;
                    }
                    mouseEntity.setType(id);
                }
            }
        } else {
            rightDown = false;
        }

    }

    private void calculateSelectionAndHighlighting() {
        if (!selectedEntity.isEmpty()) {
            eventManager.fireEvent(new UpdateEntitySelectionEvent(selectedEntity, false, false));
        }
        for (String e : entityManager.getAllEntites()) {
            if (entityManager.hasComponent(e, ColliderComponent.class)) {
                ColliderComponent collider = (ColliderComponent) entityManager.getComponent(e, ColliderComponent.class);

                if (Maths.intersects(
                    ((TransformComponent) entityManager.getComponent(e, TransformComponent.class)).getTransform().getPosition(),
                    collider.getSimpleBoundingVolume(),
                    camera.getPosition(), mousePicker.getCurrentRay()) > 0) {
                    eventManager.fireEvent(new UpdateEntitySelectionEvent(e, false, true));
                } else {
                    eventManager.fireEvent(new UpdateEntitySelectionEvent(e, false, false));
                }
            }
        }
        if (Mouse.isButtonDown(0) && mode == 1 && !alreadyClicked) {
            alreadyClicked = true;
        }
        if (!selectedEntity.isEmpty()) {
            eventManager.fireEvent(new UpdateEntitySelectionEvent(selectedEntity, true, false));
        }
    }

    private void newMouseEntity(long type) {
        Vector3f position = mousePicker.getCurrentTerrainPoint();
        if (position != null) {
            Transform t = new Transform();
            String e = entityManager.createNewEntity();
            entitySchemas.get(type).createNewEntity(t, e, entityManager);
            TransformComponent tc = (TransformComponent) entityManager.getComponent(e, TransformComponent.class);
            tc.setPosition(position);
            tc.setScale(mouseEntity.getT().getScale());
            tc.setRotation(mouseEntity.getT().getRotation());
        }
    }

    private void createSun() {
        sun = entityManager.createNewEntity();
        eventManager.fireEvent(new UpdatePositionEvent(new Vector3f(0.0f, 100.0f, 100.0f), sun));
        eventManager.fireEvent(new UpdateRotationEvent(sun, new Vector3f(0, 0, 0)));
        EmittingLightComponent lightComponent =
            (EmittingLightComponent) entityManager.addComponentToEntity(sun,
                GraphicalComponentImplementation.EMMITING_LIGHT_COMPONENT.getName());
        Transform position = ((TransformComponent) entityManager.getComponent(sun, TransformComponent.class)).getTransform();
        Light light = new Light(sun, new Vector3f(position.getPosition()), new Vector3f(1.0f, 1.0f, 1.0f), "sun");
        lightComponent.addLight(sun, new Vector3f(position.getPosition()), light);
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
            }

        }
    }

    /**
     * Save the map.
     */
    public void performSave() {
        File levelRootDirectory = null;

        try {

            levelRootDirectory = new File(EntityEditor.class.getResource("/level/").toURI());

            File levelFolder =
                new File(levelRootDirectory, CoreConstants.LEVEL_DIRECTORY_PREFIX + 3);
            levelFolder.mkdir();
            Map<String, Object> complete = new HashMap<>();
            Map<String, Object> entities = new HashMap<>();
            for (String e : entityManager.getAllEntites()) {
                if (entityManager.getEntity(e).getEntityTypeId() >= 10000) {
                    Map<String, Object> props = new HashMap<>();
                    props.put("type", entityManager.getEntity(e).getEntityTypeId());
                    for (Component c : entityManager.getAllComponents(e).values()) {
                        Map<String, Object> config = c.getConfiguration();
                        if (!config.isEmpty()) {
                            props.put(c.getComponentName(), c.getConfiguration());
                        }
                    }
                    entities.put(e, props);
                }
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
