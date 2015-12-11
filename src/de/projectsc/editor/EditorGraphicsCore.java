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

import de.projectsc.core.ComponentRegistry;
import de.projectsc.core.CoreConstants;
import de.projectsc.core.EntityManager;
import de.projectsc.core.EventManager;
import de.projectsc.core.data.Timer;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.terrain.Terrain;
import de.projectsc.core.entities.Component;
import de.projectsc.core.entities.EntityState;
import de.projectsc.core.entities.TransformComponent;
import de.projectsc.core.events.ChangeEntityStateEvent;
import de.projectsc.core.events.ChangeMeshRendererParameterEvent;
import de.projectsc.core.events.ChangePositionEvent;
import de.projectsc.core.events.ChangeScaleEvent;
import de.projectsc.core.events.NewMeshEvent;
import de.projectsc.core.events.NewTextureEvent;
import de.projectsc.core.events.RotateEvent;
import de.projectsc.core.modes.client.gui.RenderingSystem;
import de.projectsc.core.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.core.modes.client.gui.components.GraphicalComponentImplementation;
import de.projectsc.core.modes.client.gui.components.MeshRendererComponent;
import de.projectsc.core.modes.client.gui.data.Scene;
import de.projectsc.core.modes.client.gui.objects.terrain.TerrainModel;
import de.projectsc.core.modes.client.gui.objects.text.TextMaster;
import de.projectsc.core.modes.client.gui.render.MasterRenderer;
import de.projectsc.core.modes.client.gui.utils.MousePicker;
import de.projectsc.core.systems.SystemMaster;

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

    private long entity = -1;

    private EditorData editorData;

    private final BlockingQueue<String> incomingQueue;

    private long sun;

    private final AtomicBoolean doRender = new AtomicBoolean(true);

    private List<TerrainModel> terrainModels;

    private SystemMaster systemMaster;

    private RenderingSystem renderSystem;

    private boolean renderSkybox = true;

    public EditorGraphicsCore(Canvas displayParent, int width, int height, BlockingQueue<String> messageQueue) {
        incomingQueue = new LinkedBlockingQueue<>();
        this.displayParent = displayParent;
        this.width = width;
        this.height = height;

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
        renderSystem = new RenderingSystem();
        TextMaster.init();
        camera = new EditorCamera();
        systemMaster = new SystemMaster();
        systemMaster.initialize();
        createNewEntity();
        masterRenderer = new MasterRenderer();
        createSun();
        createTerrain();
        gameLoop();
    }

    private void loadGUIComponents() {
        for (GraphicalComponentImplementation it : GraphicalComponentImplementation.values()) {
            ComponentRegistry.registerComponent(it.getName(), it.getClazz());
        }
    }

    protected void gameLoop() {
        Timer.init();
        int timer = 1500;
        while (running) {
            Timer.update();
            readMessages();
            camera.move(Timer.getDelta());
            if (editorData.isLightAtCameraPostion()) {
                if (!EntityManager.getEntity(entity).getTransform().getPosition().equals(
                    camera.getPosition())) {
                    EventManager.fireEvent(new ChangePositionEvent(camera.getPosition(), sun));
                }
            }
            systemMaster.update();
            renderSystem.update();
            timer = cycleTextures(timer, Timer.getDelta());
            camera.move(Timer.getDelta());
            mousePicker.update(getTerrains(), camera.getPosition(), camera.createViewMatrix());
            if (doRender.get()) {
                Scene s = renderSystem.createScene();
                s.setTerrains(terrainModels);
                s.setRenderSkybox(renderSkybox);
                masterRenderer.renderScene(s, camera, Timer.getDelta(), new Vector4f(0, 100000000, 0, 100000000));
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
        sun = EntityManager.createNewEntity();
        EventManager.fireEvent(new ChangePositionEvent(new Vector3f(0.0f, 100.0f, 100.0f), sun));
        EventManager.fireEvent(new RotateEvent(new Vector3f(0, 0, 0), sun));
        EmittingLightComponent lightComponent =
            (EmittingLightComponent) EntityManager.addComponentToEntity(sun,
                GraphicalComponentImplementation.EMMITING_LIGHT_COMPONENT.getName());
        TransformComponent position = EntityManager.getEntity(sun).getTransform();
        Light light = new Light(new Vector3f(position.getPosition()), new Vector3f(1.0f, 1.0f, 1.0f), "sun");
        lightComponent.addLight(sun, new Vector3f(position.getPosition()), light);
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

    private int cycleTextures(int timer, long delta) {
        if (editorData.isCycleTextures()) {
            timer -= delta;
            if (timer <= 0) {
                timer = 1500;
                MeshRendererComponent component =
                    (MeshRendererComponent) EntityManager.getComponent(entity, MeshRendererComponent.NAME);
                if (entity != -1 && component != null) {
                    component.setTextureIndex((component.getTextureIndex() + 1)
                        % (editorData.getNumColums() * editorData.getNumColums()));
                }
            }
        }
        return timer;
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
        if (entity != -1) {
            EventManager.fireEvent(new ChangeScaleEvent(entity, new Vector3f(editorData.getScale(), editorData.getScale(), editorData
                .getScale())));
            EventManager.fireEvent(new ChangeMeshRendererParameterEvent(entity, editorData.isFakeLighting(), editorData
                .isTransparent(), editorData.getReflectivity(), editorData.getShineDamper(), editorData.getNumColums()));
        }
    }

    /**
     */
    public void createNewEntity() {
        if (entity != -1) {
            EntityManager.deleteEntity(entity);
        }
        if (editorData != null) {
            entity = EntityManager.createNewEntity();
            camera.bindToEntity(entity);
        } else {
            entity = -1;
            camera.setLookAtPoint(0, 0, 0);
        }
    }

    private void loadModel() {
        if (EntityManager.hasComponent(entity, MeshRendererComponent.class)) {
            EntityManager.getComponent(entity, MeshRendererComponent.NAME);
        } else {
            EntityManager.addComponentToEntity(entity, MeshRendererComponent.NAME);
        }
        try {
            EventManager.fireEvent(new NewMeshEvent(entity, editorData.getModelFile()));
            EventManager.fireEvent(new NewTextureEvent(entity,
                new File(EditorGraphicsCore.class.getResource(CoreConstants.GRAPHICS_DIRECTORY_NAME + "/white.png").toURI())));
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
                java.util.Map<String, Object> serialized =
                    mapper.readValue(tree.get("components").get(name), new HashMap<String, Object>().getClass());
                if (name.equals(TransformComponent.class)) {
                    TransformComponent t = EntityManager.getEntity(getCurrentEntity()).getTransform();
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
                    Component c = EntityManager.getComponent(getCurrentEntity(), name);
                    if (c != null) {
                        c.deserialize(serialized, schemaFile);
                    }
                }
            }
        } catch (IOException e1) {
            LOGGER.error("Could not read entity file in path " + schemaFile + ": " + e1.getMessage());

        }
        camera.bindToEntity(entity);
        updateData();
        doRender(true);

    }

    private void updateTexture() {
        EventManager.fireEvent(new NewTextureEvent(entity, editorData.getTextureFile()));
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
        EntityManager.addComponentToEntity(entity, component);
    }

    public long getCurrentEntity() {
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
            EventManager.fireEvent(new ChangeEntityStateEvent(entity, EntityState.MOVING));
        } else {
            EventManager.fireEvent(new ChangeEntityStateEvent(entity, EntityState.STANDING));
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
        EntityManager.removeComponentFromEntity(getCurrentEntity(), component);
    }

    public void setRenderSkybox(boolean selected) {
        renderSkybox = selected;
    }

}
