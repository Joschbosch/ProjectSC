/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.game.states;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.data.structure.SnapshotDelta;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.events.entity.movement.UpdatePositionEvent;
import de.projectsc.core.events.entity.movement.UpdateRotationEvent;
import de.projectsc.core.game.GameConfiguration;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.messages.GameMessageConstants;
import de.projectsc.core.systems.physics.collision.CollisionSystem;
import de.projectsc.core.systems.state.EntityStateSystem;
import de.projectsc.core.utils.MapLoader;
import de.projectsc.modes.client.core.data.ClientMessage;
import de.projectsc.modes.client.core.interfaces.ClientState;
import de.projectsc.modes.client.core.manager.ClientSnapshotManger;
import de.projectsc.modes.client.core.states.CommonClientState;
import de.projectsc.modes.client.game.system.GamePhysicsSystem;
import de.projectsc.modes.client.game.ui.controls.GameTime;
import de.projectsc.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.modes.client.gui.components.GraphicalComponentImplementation;

/**
 * State of the client when the game is running.
 * 
 * @author Josch Bosch
 */
public class GameState extends CommonClientState {

    private static Log LOGGER = LogFactory.getLog(GameState.class);

    private boolean loadingDone = false;

    private GameConfiguration gameConfig;

    private GamePhysicsSystem physicsSystem;

    private String sun;

    private EntityStateSystem stateSystem;

    private CollisionSystem collisionSystem;

    public GameState() {
        super("GameState", 0);
    }

    @Override
    public void init(BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager, ClientSnapshotManger snapshotManager, Timer timer) {
        super.init(networkQueue, entityManager, eventManager, componentManager, snapshotManager, timer);
        this.gameConfig = new GameConfiguration();
        new GameTime(timer);
    }

    @Override
    public ClientState handleMessage(ClientMessage msg) {
        if (msg.getMessage().equals(GameMessageConstants.NEW_SNAPSHOT_DELTA)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String data = (String) msg.getData()[0];
                SnapshotDelta s = mapper.readValue(data.substring(1, data.length() - 1), SnapshotDelta.class);
                snapshotManager.applyNewSnapshotDelta(s);
            } catch (IOException e) {
                LOGGER.error("Could not read snapshot delta: ", e);
            }
        } else if (msg.getMessage().equals("FullSnapshot")) {
            Snapshot s = null;
            String snapshotData = (String) msg.getData()[0];
            ObjectMapper mapper = new ObjectMapper();
            try {
                s = mapper.readValue(snapshotData.substring(1, snapshotData.length() - 1), new Snapshot().getClass());
                snapshotManager.applyNewAknowledgedSnapshot(s);
            } catch (IOException e) {
                LOGGER.error("Could not read snapshot: ", e);
            }
        }
        return null;
    }

    @Override
    public void loop(long tickTime) {
        if (!loadingDone) {
            stateSystem = new EntityStateSystem(entityManager, eventManager);
            physicsSystem = new GamePhysicsSystem(entityManager, eventManager);
            collisionSystem = new CollisionSystem(entityManager, eventManager);
            loadMap();
            createSun();
            sendMessage(new ClientMessage(GameMessageConstants.UPDATE_LOADING_PROGRESS, "100"));
            loadingDone = true;
        }
        stateSystem.update(tickTime);
        // physicsSystem.update(tickTime);
        collisionSystem.update(tickTime);
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

    private void loadMap() {
        MapLoader.loadMap(gameConfig.getMapName(), entityManager);
    }

    @Override
    public String getId() {
        return "Game";
    }

}
