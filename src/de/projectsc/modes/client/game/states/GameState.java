/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.game.states;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.codehaus.jackson.map.ObjectMapper;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.impl.physic.ColliderComponent;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.data.structure.SnapshotDelta;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.events.movement.ChangePositionEvent;
import de.projectsc.core.events.movement.ChangeRotationEvent;
import de.projectsc.core.game.GameConfiguration;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.messages.GameMessageConstants;
import de.projectsc.core.utils.MapLoader;
import de.projectsc.modes.client.core.data.ClientMessage;
import de.projectsc.modes.client.core.interfaces.ClientState;
import de.projectsc.modes.client.core.manager.ClientSnapshotManger;
import de.projectsc.modes.client.core.states.CommonClientState;
import de.projectsc.modes.client.game.system.GamePhysicsSystem;
import de.projectsc.modes.client.game.ui.controls.GameTime;
import de.projectsc.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.modes.client.gui.components.GraphicalComponentImplementation;

public class GameState extends CommonClientState {

    private boolean loadingDone = false;

    private GameTime gametimeUI;

    private GameConfiguration gameConfig;

    private GamePhysicsSystem physicsSystem;

    private String sun;

    public GameState() {
        super("GameState", 0);
    }

    @Override
    public void init(BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager, ClientSnapshotManger snapshotManager, Timer timer) {
        super.init(networkQueue, entityManager, eventManager, componentManager, snapshotManager, timer);
        this.gameConfig = new GameConfiguration();
        this.gametimeUI = new GameTime(timer);
    }

    @Override
    public ClientState handleMessage(ClientMessage msg) {
        if (msg.getMessage().equals(GameMessageConstants.NEW_SNAPSHOT)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String data = (String) msg.getData()[0];
                SnapshotDelta s = mapper.readValue(data.substring(1, data.length() - 1), SnapshotDelta.class);
                if (s.getRemoved() != null) {
                    for (String id : s.getRemoved()) {
                        entityManager.deleteEntity(id);
                    }
                }
                if (s.getCreated() != null) {
                    for (String newEntity : s.getCreated()) {
                        String[] values = newEntity.split(";");
                        if (entityManager.getEntity(values[0]) == null) {
                            String e = entityManager.createNewEntityFromSchema(Long.parseLong(values[1]), values[0]);
                            Map<String, Map<String, Double>> transformInfo =
                                mapper.readValue(values[2], new HashMap<String, Map<String, Double>>().getClass());
                            entityManager.getEntity(e).getTransform().parseTransformValues(transformInfo);
                        }
                    }
                }

            } catch (IOException e) {
            }
        } else if (msg.getMessage().equals("FullSnapshot")) {
            Snapshot s = null;
            String snapshotData = (String) msg.getData()[0];
            ObjectMapper mapper = new ObjectMapper();
            try {
                s = mapper.readValue(snapshotData.substring(1, snapshotData.length() - 1), new Snapshot().getClass());
            } catch (IOException e) {

            }
            snapshotManager.applyNewAknowledgedSnapshot(s);
        }
        return null;
    }

    @Override
    public void loop(long tickTime) {
        timer.updateGameTimeAndTick(tickTime);
        if (!loadingDone) {
            physicsSystem = new GamePhysicsSystem(entityManager, eventManager);
            loadMap();
            createSun();
            sendMessage(new ClientMessage(GameMessageConstants.UPDATE_LOADING_PROGRESS, "100"));
            loadingDone = true;
        }
        physicsSystem.update(tickTime);
    }

    private void createSun() {
        sun = entityManager.createNewEntity();
        eventManager.fireEvent(new ChangeRotationEvent(sun, new Vector3f(0, 0, 0)));
        EmittingLightComponent lightComponent =
            (EmittingLightComponent) entityManager.addComponentToEntity(sun,
                GraphicalComponentImplementation.EMMITING_LIGHT_COMPONENT.getName());
        Transform position = entityManager.getEntity(sun).getTransform();
        Light light = new Light(new Vector3f(position.getPosition()), new Vector3f(1.0f, 1.0f, 1.0f), "sun");
        lightComponent.addLight(sun, new Vector3f(position.getPosition()), light);
        entityManager.addComponentToEntity(sun, ColliderComponent.NAME);
        eventManager.fireEvent(new ChangePositionEvent(new Vector3f(0.0f, 100.0f, 100.0f), sun));

    }

    private void loadMap() {
        MapLoader.loadMap(gameConfig.getMapName(), entityManager);
    }

    @Override
    public String getId() {
        return "Game";
    }

}
