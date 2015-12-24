/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.states;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.codehaus.jackson.map.ObjectMapper;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.impl.physic.ColliderComponent;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.events.movement.ChangePositionEvent;
import de.projectsc.core.events.movement.ChangeRotationEvent;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.messages.GameMessageConstants;
import de.projectsc.core.utils.MapLoader;
import de.projectsc.modes.client.core.data.ClientGameContext;
import de.projectsc.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.modes.client.gui.components.GraphicalComponentImplementation;
import de.projectsc.modes.client.interfaces.ClientState;
import de.projectsc.modes.client.interfaces.GUI;
import de.projectsc.modes.client.messages.ClientMessage;
import de.projectsc.modes.client.ui.BasicUIElement;
import de.projectsc.modes.client.ui.elements.GameTime;

public class GameState extends CommonClientState {

    private boolean loadingDone = false;

    private Snapshot currentSnapshot = null;

    private GameTime gametimeUI;

    @Override
    public void init(GUI gui, BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager, ClientGameContext gameData) {
        super.init(gui, networkQueue, entityManager, eventManager, componentManager, gameData);
        gametimeUI = new GameTime();
        gui.initState(this);
    }

    @Override
    public ClientState handleMessage(ClientMessage msg) {
        if (msg.getMessage().equals(GameMessageConstants.NEW_SNAPSHOT)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String data = (String) msg.getData()[0];
                Snapshot s = mapper.readValue(data.substring(1, data.length() - 1), Snapshot.class);
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
                currentSnapshot = s;
                gametimeUI.setGameTime(s.getGameTime());
            } catch (IOException e) {
            }
        }
        return null;
    }

    @Override
    public void loop(long tickTime) {
        if (!loadingDone) {
            loadMap();
            gui.loadTerrain();
            createSun();
            sendMessage(new ClientMessage(GameMessageConstants.UPDATE_LOADING_PROGRESS, "100"));
            loadingDone = true;
        }
    }

    private void createSun() {
        String sun = entityManager.createNewEntity();
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

    private void loadMap() {
        MapLoader.loadMap(gameData.getMapName(), entityManager);
    }

    @Override
    public List<BasicUIElement> getUI() {
        List<BasicUIElement> ui = new LinkedList<>();
        ui.add(gametimeUI);
        return ui;
    }

    @Override
    public Snapshot getSnapshot() {
        return currentSnapshot;
    }

}
