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
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.rits.cloning.Cloner;

import de.projectsc.EntityEditor;
import de.projectsc.client.core.data.Scene;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.render.MasterRenderer;
import de.projectsc.client.gui.terrain.TerrainModel;
import de.projectsc.client.gui.tools.MousePicker;
import de.projectsc.core.CoreConstants;
import de.projectsc.core.Terrain;
import de.projectsc.core.components.Component;
import de.projectsc.core.components.ComponentType;
import de.projectsc.core.components.impl.BoundingComponent;
import de.projectsc.core.components.impl.EmittingLightComponent;
import de.projectsc.core.components.impl.ModelAndTextureComponent;
import de.projectsc.core.components.impl.MovingComponent;
import de.projectsc.core.components.impl.ParticleEmitterComponent;
import de.projectsc.core.entities.Entity;

/**
 * Core class for the GUI.
 * 
 * @author Josch Bosch
 */
public class MapEditorGraphicsCore implements Runnable {

    private static final String SLASHED_MODEL_DIR = "/" + CoreConstants.SCHEME_DIRECTORY_NAME + "/";

    private static final String SEPARATOR = ";";

    private static final Log LOGGER = LogFactory.getLog(MapEditorGraphicsCore.class);

    private boolean running;

    private final Canvas displayParent;

    private final int width;

    private final int height;

    private Camera camera;

    private MousePicker mousePicker;

    private MasterRenderer masterRenderer;

    private final List<Entity> entities = new LinkedList<>();

    private ModelAndTextureComponent modelComponent;

    private final BlockingQueue<String> incomingQueue;

    private Light sun;

    private final AtomicBoolean doRender = new AtomicBoolean(true);

    private final Map<Long, Entity> entitySchemas = new TreeMap<>();

    private Entity entitySchemaAtCursor;

    private boolean alreadyClicked;

    private int mode;

    private Entity selectedEntity;

    private List<Terrain> terrains;

    public MapEditorGraphicsCore(Canvas displayParent, int width, int height, BlockingQueue<String> messageQueue) {
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

        camera = new Camera();
        camera.setLookAtPoint(0, 0, 0);
        masterRenderer = new MasterRenderer();
        createSun();
        loadEntitySchemas();
        entitySchemaAtCursor = entitySchemas.get(10000L);
        entities.add(entitySchemaAtCursor);
        selectedEntity = null;
        gameLoop();
    }

    private void loadEntitySchemas() {
        try {
            File folder = new File(EntityEditor.class.getResource(SLASHED_MODEL_DIR).toURI());
            for (File schemaDir : folder.listFiles()) {
                if (schemaDir.getName().matches(CoreConstants.SCHEME_DIRECTORY_PREFIX + "\\d{5}")) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode tree;
                    try {
                        tree = mapper.readTree(new File(schemaDir, CoreConstants.ENTITY_FILENAME));

                        int id = Integer.parseInt(schemaDir.getName().substring(1));
                        Entity newEntitySchema = new Entity(id);
                        if (new File(schemaDir, CoreConstants.MODEL_FILENAME).exists()) {
                            File texture = null;
                            if (new File(schemaDir, CoreConstants.TEXTURE_FILENAME).exists()) {
                                texture = new File(schemaDir, CoreConstants.TEXTURE_FILENAME);
                            } else {
                                texture = null; // load standard texture;
                            }
                            loadModel(newEntitySchema, new File(schemaDir, CoreConstants.MODEL_FILENAME), texture);
                            ModelAndTextureComponent matc = newEntitySchema.getComponent(ModelAndTextureComponent.class);
                            if (tree.get("fakeLighting") != null) {
                                matc.setFakeLighting(tree.get("fakeLighting").getBooleanValue());
                            }
                            matc.setNumberOfRows(tree.get("numColumns").getIntValue());
                            matc.setReflectivity((float) tree.get("reflectivity").getDoubleValue());
                            matc.setShineDamper((float) tree.get("shineDamper").getDoubleValue());
                            newEntitySchema.setScale((float) tree.get("scale").getDoubleValue());
                            Iterator<String> componentNamesIterator = tree.get("components").getFieldNames();
                            newEntitySchema.setPosition(new Vector3f(0, 0, 0));
                            newEntitySchema.setRotation(new Vector3f(0, 0, 0));
                            while (componentNamesIterator.hasNext()) {
                                String name = componentNamesIterator.next();
                                addComponent(newEntitySchema, name, tree.get("components").get(name), schemaDir);
                            }
                            entitySchemas.put(newEntitySchema.getEntityTypeId(), newEntitySchema);
                        }
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                }
            }
        } catch (URISyntaxException e1) {
            LOGGER.error(e1);
        }
    }

    /**
     * Add new component to entity.
     * 
     * @param newEntitySchema to add component
     * @param component to add
     * @param jsonNode with information
     * @param schemaDir of entity
     * @throws IOException e
     * @throws JsonProcessingException e
     */
    public void addComponent(Entity newEntitySchema, String component, JsonNode jsonNode, File schemaDir) throws JsonProcessingException,
        IOException {
        Component newComponent = null;
        if (EmittingLightComponent.NAME.equals(component)) {
            newComponent = new EmittingLightComponent(newEntitySchema);
        }
        if (MovingComponent.NAME.equals(component)) {
            newComponent = new MovingComponent(newEntitySchema);
        }
        if (BoundingComponent.NAME.equals(component)) {
            newComponent = new BoundingComponent(newEntitySchema);
        }
        if (ParticleEmitterComponent.NAME.equals(component)) {
            newComponent = new ParticleEmitterComponent(newEntitySchema);
        }
        newComponent.deserialize(jsonNode, schemaDir);
        newEntitySchema.addComponent(newComponent);
    }

    protected void gameLoop() {
        long time = System.currentTimeMillis();
        while (running) {
            long now = System.currentTimeMillis();
            long delta = now - time;
            time = now;
            readMessages();

            camera.move(delta);
            if (terrains != null) {
                mousePicker.update(terrains);
                readInput();
                for (ComponentType type : ComponentType.values()) {
                    for (Entity e : entities) {
                        e.update(type);
                    }
                }
                if (camera != null && mousePicker.getCurrentRay() != null) {
                    Entity highlightingEntity = null;
                    float tMin = Float.MAX_VALUE;
                    for (Entity e : entities) {
                        if (e.hasComponent(BoundingComponent.class)) {
                            float t = e.getComponent(BoundingComponent.class).intersects(camera.getPosition(),
                                mousePicker.getCurrentRay());
                            if (t > 0 && t < tMin) {
                                tMin = t;
                                highlightingEntity = e;
                            }
                        }
                        e.setHighlighted(false);
                    }
                    if (highlightingEntity != null) {
                        highlightingEntity.setHighlighted(true);
                    }
                }
                if (mousePicker.getCurrentTerrainPoint() != null) {
                    entitySchemaAtCursor.setPosition(mousePicker.getCurrentTerrainPoint());
                }
                if (doRender.get()) {
                    Scene s = new Scene();
                    s.setTerrain(getTerrainModels(terrains));
                    prepareEntities(s);
                    masterRenderer.renderScene(s, camera, delta, new Vector4f(0, 1, 0, 100000));
                }
            } else {
                if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                    GL11.glClearColor(1f, 1f, 1f, 0);
                } else {
                    GL11.glClearColor(1f, 1f, 0f, 0f);
                }
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }
            Display.sync(60);
            Display.update();
        }
        Display.destroy();
    }

    private List<TerrainModel> getTerrainModels(List<Terrain> terrains) {
        List<TerrainModel> terrainModels = new LinkedList<>();
        for (Terrain t : terrains) {
            terrainModels.add(t.getModel());
        }
        return terrainModels;
    }

    private void prepareEntities(Scene s) {
        Map<TexturedModel, List<Entity>> map = new HashMap<>();
        for (Entity e : entities) {
            ModelAndTextureComponent c = (ModelAndTextureComponent) e.getComponentByName(ModelAndTextureComponent.NAME);
            if (c != null) {
                List<Entity> list = map.get(c.getTexturedModel());
                if (list == null) {
                    list = new LinkedList<>();
                    map.put(c.getTexturedModel(), list);
                }
                list.add(e);
            }
        }
        s.setEntities(map);
    }

    private void readInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            entitySchemaAtCursor.setScale(entitySchemaAtCursor.getScale() + 0.5f);
            if (entitySchemaAtCursor.hasComponent(BoundingComponent.class)) {
                BoundingComponent component = entitySchemaAtCursor.getComponent(BoundingComponent.class);
                component.setScale(component.getScale() + 0.5f);
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
            if (entitySchemaAtCursor.getScale() > 0.5) {
                entitySchemaAtCursor.setScale(entitySchemaAtCursor.getScale() - 0.5f);
                if (entitySchemaAtCursor.hasComponent(BoundingComponent.class)) {
                    BoundingComponent component = entitySchemaAtCursor.getComponent(BoundingComponent.class);
                    component.setScale(component.getScale() - 0.5f);
                }
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            entitySchemaAtCursor.setRotY(entitySchemaAtCursor.getRotY() + 1f);
            if (entitySchemaAtCursor.hasComponent(BoundingComponent.class)) {
                BoundingComponent component = entitySchemaAtCursor.getComponent(BoundingComponent.class);
                component.setOffsetRotation(new Vector3f(component.getOffsetRotation().x, component.getOffsetRotation().y + 1f, component
                    .getOffsetRotation().z));
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            entitySchemaAtCursor.setRotY(entitySchemaAtCursor.getRotY() - 1f);
            if (entitySchemaAtCursor.hasComponent(BoundingComponent.class)) {
                BoundingComponent component = entitySchemaAtCursor.getComponent(BoundingComponent.class);
                component.setOffsetRotation(new Vector3f(component.getOffsetRotation().x, component.getOffsetRotation().y - 1f, component
                    .getOffsetRotation().z));
            }
        }
        if (Mouse.isButtonDown(0) && mode == 0 && !alreadyClicked) {
            alreadyClicked = true;
            Entity newEntity = Cloner.standard().deepClone(entitySchemaAtCursor);
            entities.add(newEntity);
        }
        if (Mouse.isButtonDown(0) && mode == 1 && !alreadyClicked) {
            alreadyClicked = true;
            for (Entity e : entities) {
                if (e.hasComponent(BoundingComponent.class)) {
                    if (e.getComponent(BoundingComponent.class).intersects(camera.getPosition(), mousePicker.getCurrentRay()) > 0) {
                        if (selectedEntity != null) {
                            selectedEntity.setSelected(false);
                        }
                        selectedEntity = e;
                        selectedEntity.setSelected(true);
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
                    entities.remove(entitySchemaAtCursor);
                    long id = entitySchemaAtCursor.getEntityTypeId();
                    id = (id + 1) % 1000000;
                    while (entitySchemas.get(id) == null) {
                        id = (id + 1) % 1000000;
                    }
                    entitySchemaAtCursor = entitySchemas.get(id);
                    entities.add(entitySchemaAtCursor);
                }
            }
        }

    }

    private void createSun() {
        Entity lightEntity = new Entity(-2);
        lightEntity.setPosition(new Vector3f(0, 0, 0));
        EmittingLightComponent lightComponent = new EmittingLightComponent(lightEntity);
        sun = new Light(new Vector3f(0.0f, 500.0f, 500.0f), new Vector3f(1.0f, 1.0f, 1.0f), "sun");
        lightComponent.addLight(lightEntity, sun);
        lightEntity.addComponent(lightComponent);
        entities.add(lightEntity);
    }

    private void createTerrain(int k, int l, String parsed) {
        String texture = "terrain/grass.png";
        terrains = new LinkedList<>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Terrain terrain =
                    new Terrain(-0.5f * i, -0.5f * j, texture, texture, texture, texture);
                terrains.add(terrain);
            }
        }

        mousePicker = new MousePicker(camera, masterRenderer.getProjectionMatrix());
    }

    protected void initGL() {}

    private void loadModel(Entity e, File modelFile, File texFile) {
        modelComponent = new ModelAndTextureComponent(e);
        modelComponent.loadModel(modelFile, texFile);
        e.addComponent(modelComponent);
        modelComponent.loadAndApplyTexture(texFile);

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
                entities.remove(selectedEntity);
                entities.add(entitySchemaAtCursor);
            }

        } else {
            if (this.mode != 1) {
                this.mode = 1;
                entities.remove(entitySchemaAtCursor);
                if (selectedEntity != null) {
                    entities.add(selectedEntity);
                }
            }

        }
    }

    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    /**
     * unselect entity.
     */
    public void removeSelectedEntity() {
        if (selectedEntity != null) {
            entities.remove(selectedEntity);
        }
    }
}
