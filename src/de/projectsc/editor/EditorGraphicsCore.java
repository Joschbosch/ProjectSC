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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.entities.states.EntityStates;
import de.projectsc.core.events.entity.movement.UpdatePositionEvent;
import de.projectsc.core.events.entity.movement.UpdateRotationEvent;
import de.projectsc.core.events.entity.movement.UpdateScaleEvent;
import de.projectsc.core.events.entity.objects.UpdateMeshEvent;
import de.projectsc.core.events.entity.state.UpdateEntityStateEvent;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.physics.BasicPhysicsSystem;
import de.projectsc.core.systems.physics.collision.CollisionSystem;
import de.projectsc.core.systems.state.EntityStateSystem;
import de.projectsc.core.terrain.Terrain;
import de.projectsc.modes.client.gui.InputSystem;
import de.projectsc.modes.client.gui.RenderingSystem;
import de.projectsc.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.modes.client.gui.components.GraphicalComponentImplementation;
import de.projectsc.modes.client.gui.components.MeshRendererComponent;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.events.UpdateMeshRendererParameterEvent;
import de.projectsc.modes.client.gui.events.UpdateTextureEvent;
import de.projectsc.modes.client.gui.input.InputConsumeManager;
import de.projectsc.modes.client.gui.objects.particles.ParticleMaster;
import de.projectsc.modes.client.gui.objects.terrain.TerrainModel;
import de.projectsc.modes.client.gui.objects.text.TextMaster;
import de.projectsc.modes.client.gui.render.MasterRenderer;
import de.projectsc.modes.client.gui.utils.GUIConstants;
import de.projectsc.modes.client.gui.utils.MousePicker;

/**
 * Core class for the GUI.
 * 
 * @author Josch Bosch
 */
public class EditorGraphicsCore implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(EditorGraphicsCore.class);

    private boolean running;

    private final Canvas displayParent;

    private final int width;

    private final int height;

    private EditorCamera camera;

    private MousePicker mousePicker;

    private MasterRenderer masterRenderer;

    private String entity = "";

    private EditorData editorData;

    private final BlockingQueue<String> incomingQueue;

    private String sun;

    private final AtomicBoolean doRender = new AtomicBoolean(true);

    private List<TerrainModel> terrainModels;

    private RenderingSystem renderSystem;

    private boolean renderSkybox = true;

    private BasicPhysicsSystem physicsSystem;

    private ComponentManager componentManager;

    private EntityManager entityManager;

    private EventManager eventManager;

    private InputConsumeManager inputConsumeManager;

    private InputSystem inputSystem;

    private Timer timer;

    private CollisionSystem collisionSystem;

    public EditorGraphicsCore(Canvas displayParent, int width, int height, BlockingQueue<String> messageQueue,
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
            initGL();
        } catch (LWJGLException e) {
        }
        loadGUIComponents();
        new EntityStateSystem(entityManager, eventManager);
        physicsSystem = new BasicPhysicsSystem(entityManager, eventManager);
        collisionSystem = new CollisionSystem(entityManager, eventManager);
        renderSystem = new RenderingSystem(entityManager, eventManager);
        this.inputSystem = new InputSystem();
        TextMaster.init();
        camera = new EditorCamera();
        inputConsumeManager.addListener(camera);
        createNewEntity();
        masterRenderer = new MasterRenderer();
        createSun();
        createTerrain();
        gameLoop();
    }

    private void loadGUIComponents() {
        for (GraphicalComponentImplementation it : GraphicalComponentImplementation.values()) {
            componentManager.registerComponent(it.getName(), it.getClazz());
        }
    }

    protected void gameLoop() {
        timer.init();
        while (running) {
            timer.update();
            readMessages();
            inputConsumeManager.processInput(inputSystem.updateInputs());
            camera.move(timer.getDelta());
            ParticleMaster.update(timer.getDelta(), camera.getPosition());
            physicsSystem.update(timer.getDelta());
            collisionSystem.update(timer.getDelta());
            renderSystem.update(timer.getDelta());
            cycleTextures(0, timer.getDelta());
            mousePicker.update(getTerrains(), camera.getPosition(), camera.createViewMatrix());
            if (doRender.get()) {
                GUIScene s = renderSystem.createScene();
                s.setTerrains(terrainModels);
                s.setRenderSkybox(renderSkybox);
                masterRenderer.renderScene(s, camera, timer.getDelta(), new Vector4f(0, 100000000, 0, 100000000));

            }
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

    private void createSun() {
        sun = entityManager.createNewEntity();
        eventManager.fireEvent(new UpdatePositionEvent(new Vector3f(100000.0f, 100000.0f, 100000.0f), sun));
        eventManager.fireEvent(new UpdateRotationEvent(sun, new Vector3f(0, 0, 0)));
        EmittingLightComponent lightComponent =
            (EmittingLightComponent) entityManager.addComponentToEntity(sun,
                GraphicalComponentImplementation.EMMITING_LIGHT_COMPONENT.getName());
        Transform position = entityManager.getEntity(sun).getTransform();
        Light light = new Light(new Vector3f(position.getPosition()), new Vector3f(1.0f, 1.0f, 1.0f), "sun");
        light.setAttenuation(new Vector3f(1, 0, 0));
        lightComponent.addLight(sun, new Vector3f(position.getPosition()), light);
        entityManager.addComponentToEntity(sun, ColliderComponent.NAME);
    }

    private void createTerrain() {
        String texture = "terrain/grass.png";
        terrainModels = new LinkedList<>();
        for (int i = -5; i < 5; i++) {
            for (int j = -5; j < 5; j++) {
                Terrain terrain =
                    new Terrain(i, j, texture, texture, texture, texture);
                TerrainModel model = new TerrainModel(terrain);
                terrainModels.add(model);
            }
        }

        mousePicker = new MousePicker(masterRenderer.getProjectionMatrix());
    }

    private int cycleTextures(int timing, long delta) {
        if (editorData.isCycleTextures()) {
            timing -= delta;
            if (timing <= 0) {
                timing = 1500;
                MeshRendererComponent component =
                    (MeshRendererComponent) entityManager.getComponent(entity, MeshRendererComponent.NAME);
                if (!entity.isEmpty() && component != null) {
                    component.setTextureIndex((component.getTextureIndex() + 1)
                        % (editorData.getNumColums() * editorData.getNumColums()));
                }
            }
        }
        return timing;
    }

    protected void initGL() {}

    /**
     * Update data from current editor data.
     */
    public void updateData() {
        if (editorData != null) {
            camera.setRotateCamera(editorData.isRotateCamera());
            updateData(editorData);
        }
    }

    /**
     * Update data from new data.
     * 
     * @param data to update
     */
    public void updateData(EditorData data) {
        if (!entity.isEmpty()) {
            eventManager.fireEvent(new UpdateScaleEvent(entity, new Vector3f(editorData.getScale(), editorData.getScale(), editorData
                .getScale())));
            eventManager.fireEvent(new UpdateMeshRendererParameterEvent(entity, editorData.isFakeLighting(), editorData
                .isTransparent(), editorData.getReflectivity(), editorData.getShineDamper(), editorData.getNumColums()));
        }
    }

    /**
     */
    public void createNewEntity() {
        if (!entity.isEmpty()) {
            entityManager.deleteEntity(entity);
        }
        if (editorData != null) {
            entity = entityManager.createNewEntity();
            camera.bindToEntity(entityManager.getEntity(entity));
        } else {
            entity = "";
            camera.setLookAtPoint(0, 0, 0);
        }
    }

    private void loadModel() {
        if (entityManager.hasComponent(entity, MeshRendererComponent.class)) {
            entityManager.getComponent(entity, MeshRendererComponent.NAME);
        } else {
            entityManager.addComponentToEntity(entity, MeshRendererComponent.NAME);
        }
        try {
            eventManager.fireEvent(new UpdateMeshEvent(entity, editorData.getModelFile()));
            eventManager.fireEvent(new UpdateTextureEvent(entity,
                new File(EditorGraphicsCore.class.getResource(GUIConstants.TEXTURE_ROOT + GUIConstants.BASIC_TEXTURE_WHITE).toURI())));
        } catch (URISyntaxException e) {
            LOGGER.error(e);
        }
        if (editorData.getTextureFile() != null) {
            triggerUpdateTexture();
        }
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
            if (msg.startsWith("deserialize")) {
                deserialize(msg);
            }
        }
    }

    @SuppressWarnings({ "unchecked" })
    private void deserialize(String msg) {
        File schemaFile = new File(msg.substring(msg.indexOf(":") + 1));
        doRender(false);
        ObjectMapper mapper = new ObjectMapper();
        try {
            File entityFile = new File(schemaFile, CoreConstants.ENTITY_FILENAME);
            JsonNode tree = mapper.readTree(entityFile);
            editorData.setId(Integer.parseInt(schemaFile.getName().substring(1)));
            Iterator<String> componentNamesIterator = tree.get("components").getFieldNames();
            while (componentNamesIterator.hasNext()) {
                String name = componentNamesIterator.next();
                Map<String, Object> serialized =
                    mapper.readValue(tree.get("components").get(name), new HashMap<String, Object>().getClass());
                if (name.equals(TransformComponent.class)) {
                    Transform t = entityManager.getEntity(getCurrentEntity()).getTransform();
                    Vector3f position = new Vector3f();
                    Vector3f rotation = new Vector3f();
                    Vector3f scale = new Vector3f();
                    position.x = (float) (double) serialized.get("positionX");
                    position.y = (float) (double) serialized.get("positionY");
                    position.z = (float) (double) serialized.get("positionZ");
                    position.x = (float) (double) serialized.get("rotationX");
                    rotation.y = (float) (double) serialized.get("rotationY");
                    rotation.z = (float) (double) serialized.get("rotationZ");
                    scale.x = (float) (double) serialized.get("scaleX");
                    scale.y = (float) (double) serialized.get("scaleY");
                    scale.z = (float) (double) serialized.get("scaleZ");
                    t.setPosition(position);
                    t.setRotation(rotation);
                    t.setScale(scale);
                } else {
                    addComponent(name);
                    Component c = entityManager.getComponent(getCurrentEntity(), name);
                    if (c != null) {
                        c.deserialize(serialized, schemaFile.getAbsolutePath());
                    }
                }
            }
        } catch (IOException e1) {
            LOGGER.error("Could not read entity file in path " + schemaFile + ": " + e1.getMessage());

        }
        camera.bindToEntity(entityManager.getEntity(entity));
        updateData();
        doRender(true);

    }

    private void updateTexture() {
        eventManager.fireEvent(new UpdateTextureEvent(entity, editorData.getTextureFile()));
    }

    /**
     * Stop.
     */
    public void stop() {
        running = false;
    }

    public void setEditorData(EditorData data) {
        this.editorData = data;
    }

    /**
     * Add new component to entity.
     * 
     * @param component to add
     */
    public void addComponent(String component) {
        entityManager.addComponentToEntity(entity, component);
    }

    public String getCurrentEntity() {
        return entity;
    }

    /**
     * @param value true, if rendering is enabled.
     */
    public void doRender(boolean value) {
        doRender.set(value);
    }

    /**
     * @param value true, if entity should be moved
     */
    public void moveEntity(boolean value) {
        if (value) {
            eventManager.fireEvent(new UpdateEntityStateEvent(entity, EntityStates.MOVING));
        } else {
            eventManager.fireEvent(new UpdateEntityStateEvent(entity, EntityStates.IDLING));
        }
    }

    /**
     * triggers to load model in gui thread.
     */
    public void triggerLoadModel() {
        incomingQueue.offer("loadModel");
    }

    /**
     * triggers to load texture in gui thread.
     */
    public void triggerUpdateTexture() {
        incomingQueue.offer("updateTexture");
    }

    /**
     * Load schema.
     * 
     * @param schemaDir to load
     */
    public void triggerDeserialze(File schemaDir) {
        incomingQueue.offer("deserialize:" + schemaDir.getAbsolutePath());

    }

    /**
     * trigger.
     */
    public void triggerLoadBoundingBox() {
        incomingQueue.offer("LoadBoundingBox");
    }

    /**
     * Remove a component from an entity.
     * 
     * @param component to remove
     */
    public void removeComponent(String component) {
        entityManager.removeComponentFromEntity(getCurrentEntity(), component);
    }

    public void setRenderSkybox(boolean selected) {
        renderSkybox = selected;
    }

}
