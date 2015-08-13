/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.editor;

import java.awt.Canvas;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.render.MasterRenderer;
import de.projectsc.client.gui.terrain.TerrainModel;
import de.projectsc.client.gui.textures.TerrainTexture;
import de.projectsc.client.gui.textures.TerrainTexturePack;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.MousePicker;
import de.projectsc.core.CoreConstants;
import de.projectsc.core.Terrain;
import de.projectsc.core.TerrainLoader;
import de.projectsc.core.Tile;
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
public class Editor3DCore implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(Editor3DCore.class);

    private boolean running;

    private final Canvas displayParent;

    private final int width;

    private final int height;

    private Loader loader;

    private EditorCamera camera;

    private MousePicker mousePicker;

    private TerrainModel terrainModel;

    private MasterRenderer masterRenderer;

    private final List<Entity> entities = new LinkedList<>();

    private Entity entity;

    private EditorData editorData;

    private ModelAndTextureComponent modelComponent;

    private final BlockingQueue<String> incomingQueue;

    private Light sun;

    private final AtomicBoolean doRender = new AtomicBoolean(true);

    private final AtomicBoolean moveEntity = new AtomicBoolean(false);

    public Editor3DCore(Canvas displayParent, int width, int height, BlockingQueue<String> messageQueue) {
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

        camera = new EditorCamera();
        loader = new Loader();
        createNewEntity();
        masterRenderer = new MasterRenderer(loader);
        createPlayerEntity();
        createSun();
        createTerrain();
        gameLoop();
    }

    protected void gameLoop() {
        long time = System.currentTimeMillis();
        int timer = 1500;
        while (running) {
            long now = System.currentTimeMillis();
            long delta = now - time;
            time = now;
            readMessages();
            camera.move(delta);
            if (editorData.isLightAtCameraPostion()) {
                sun.setPosition(camera.getPosition());
            }
            timer = cycleTextures(timer, delta);
            moveEntity();
            for (ComponentType type : ComponentType.values()) {
                for (Entity e : entities) {
                    e.update(type);
                }
            }
            if (terrainModel != null) {
                camera.move(delta);
                mousePicker.update();
                if (doRender.get()) {
                    masterRenderer.renderScene(terrainModel, entities,
                        camera, delta, new Vector4f(0, 1, 0, 100000));
                }
            } else {
                if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                    GL11.glClearColor(1f, 0, 0, 0);
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

    private void createPlayerEntity() {
        // Entity player = new Entity(10000);
        // ModelAndTextureComponent modelAndTexture = new ModelAndTextureComponent();
        // player.addComponent(modelAndTexture);
        // modelAndTexture.loadModel(loader, player);
        // Vector3f position = entity.getPosition();
        // position.x += 40;
        // player.setPosition(position);
    }

    private void createSun() {
        Entity lightEntity = new Entity(-2);
        lightEntity.setPosition(new Vector3f(0, 0, 0));
        EmittingLightComponent lightComponent = new EmittingLightComponent();
        sun = new Light(new Vector3f(0.0f, 100.0f, 100.0f), new Vector3f(1.0f, 1.0f, 1.0f), "sun");
        lightComponent.addLight(lightEntity, sun);
        lightEntity.addComponent(lightComponent);
        entities.add(lightEntity);
    }

    private void createTerrain() {
        Tile[][] tiles = new Tile[200][200];
        String texture = "terrain/grass.png";
        Terrain t =
            new Terrain(tiles, texture, texture, texture, texture, new LinkedList<>(),
                new HashMap<Integer, Entity>());
        TerrainTexture backgroundTex = new TerrainTexture(loader.loadTexture(texture));
        TerrainTexture rTex = new TerrainTexture(loader.loadTexture(texture));
        TerrainTexture gTex = new TerrainTexture(loader.loadTexture(texture));
        TerrainTexture bTex = new TerrainTexture(loader.loadTexture(texture));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTex, rTex, gTex, bTex);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(TerrainLoader.createBlendMap(t)));

        terrainModel = new TerrainModel(t, -0.5f, -0.5f, texturePack, blendMap, loader);

        mousePicker = new MousePicker(camera, masterRenderer.getProjectionMatrix(), terrainModel);
    }

    private void moveEntity() {
        MovingComponent c = entity.getComponent(MovingComponent.class);
        if (moveEntity.get() && c != null) {
            c.setCurrentSpeed(c.getMovementSpeed());
        } else if (c != null) {
            c.setCurrentSpeed(0);
        }
        if (entity.getPosition().z >= 100) {
            entity.setPosition(new Vector3f(entity.getPosition().x, entity.getPosition().y, 0));
        }
    }

    private int cycleTextures(int timer, long delta) {
        if (editorData.isCycleTextures()) {
            timer -= delta;
            if (timer <= 0) {
                timer = 1500;
                ModelAndTextureComponent component = entity.getComponent(ModelAndTextureComponent.class);
                if (entity != null && component != null) {
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
        if (entity != null) {
            entity.setScale(editorData.getScale());
        }
        if (modelComponent != null) {
            modelComponent.setFakeLighting(editorData.isFakeLighting());
            modelComponent.setIsTransparent(editorData.isTransparent());
            modelComponent.setReflectivity(editorData.getReflectivity());
            modelComponent.setShineDamper(editorData.getShineDamper());
            modelComponent.setNumberOfRows(editorData.getNumColums());
        }
    }

    /**
     * Creates a new entity.
     */
    public void createNewEntity() {
        if (entity != null) {
            entities.remove(entity);
        }
        if (editorData != null) {
            entity = new Entity(0);
            entity.setPosition(new Vector3f(0, 0, 0));
            entity.setRotation(new Vector3f(0, 0, 0));
            entities.add(entity);
            camera.bindToEntity(entity);
        } else {
            entity = null;
            camera.setLookAtPoint(0, 0, 0);
        }
    }

    private void loadModel() {
        modelComponent = new ModelAndTextureComponent();
        try {
            modelComponent.loadModel(loader, editorData.getModelFile(),
                new File(Editor3DCore.class.getResource(CoreConstants.GRAPHICS_DIRECTORY_NAME + "/white.png").toURI()));
        } catch (URISyntaxException e) {
            LOGGER.error(e);
        }
        entity.addComponent(modelComponent);
        if (editorData.getTextureFile() != null) {
            updateTexture();
        }
    }

    /**
     * load new texture.
     */
    public void updateTexture() {
        if (modelComponent != null) {
            modelComponent.loadAndApplyTexture(loader, editorData.getTextureFile());
            updateData(editorData);
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
            if (msg.equals("LoadBoundingBox")) {
                BoundingComponent c = getCurrentEntity().getComponent(BoundingComponent.class);
                c.loadBoundingBox(entity, loader, c.getBoxFile());
            }
            if (msg.equals("particleEmitter")) {
                ParticleEmitterComponent c = getCurrentEntity().getComponent(ParticleEmitterComponent.class);
                c.createNewEmitter(loader, new Vector3f(0, 10, 0));
            }
        }
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
        if (EmittingLightComponent.NAME.equals(component)) {
            EmittingLightComponent lightComponent = new EmittingLightComponent();
            entity.addComponent(lightComponent);
        }
        if (MovingComponent.NAME.equals(component)) {
            entity.addComponent(new MovingComponent());
        }
        if (BoundingComponent.NAME.equals(component)) {
            entity.addComponent(new BoundingComponent());
        }
        if (ParticleEmitterComponent.NAME.equals(component)) {
            entity.addComponent(new ParticleEmitterComponent());
            triggerAddParticleEmitter();
        }
    }

    /**
     * Remove component from entity.
     * 
     * @param component to remove
     */
    public void removeComponent(String component) {
        entity.removeComponent(component);
    }

    /**
     * @param name to get
     * @return component
     */
    public Component getComponent(String name) {
        for (Component c : entity.getComponents().values()) {
            if (c.getComponentName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public Entity getCurrentEntity() {
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
        moveEntity.set(value);
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

    private void triggerAddParticleEmitter() {
        incomingQueue.offer("particleEmitter");
    }

    public void triggerLoadBoundingBox() {
        incomingQueue.offer("LoadBoundingBox");
    }
}
