/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rits.cloning.Cloner;

import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.data.structure.SnapshotDelta;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;

/**
 * Manages incoming Snapshots and Snapshots deltas for the client.
 * 
 * @author Josch Bosch
 */
public class ClientSnapshotManger {

    private List<Snapshot> currentSnapshots = new ArrayList<>();

    private EntityManager entityManager;

    private Timer timer;

    public ClientSnapshotManger(EntityManager entityManager, Timer timer) {
        this.entityManager = entityManager;
        this.timer = timer;
    }

    /**
     * Returns the two snapshots that must be interpolated.
     * 
     * @param tick current6
     * @return snapshots
     */
    public Snapshot[] getSnapshotsForInterpolation(long tick) {
        if (currentSnapshots.size() < 2) {
            for (int i = 0; i < currentSnapshots.size() - 1; i++) {
                if (currentSnapshots.get(i).getTick() < tick
                    && currentSnapshots.get(i + 1).getTick() > tick) {
                    Snapshot[] snapshots = new Snapshot[2];
                    snapshots[0] = currentSnapshots.get(i);
                    snapshots[1] = currentSnapshots.get(i + 1);
                    return snapshots;
                }
            }
        }
        return null;
    }

    /**
     * Add new snapshot.
     * 
     * @param s to add.
     */
    public void applyNewAknowledgedSnapshot(Snapshot s) {
        currentSnapshots.add(s);
        deserializeSnapshot(s);
        if (currentSnapshots.size() > 5) {
            currentSnapshots.remove(0);
        }

    }

    private Snapshot getCurrentSnapshot() {
        return currentSnapshots.get(currentSnapshots.size() - 1);
    }

    private void deserializeSnapshot(Snapshot s) {
        for (String id : s.getEntitiesSerialized().keySet()) {
            Map<String, Component> components = entityManager.getAllComponents(id);
            for (Component c : components.values()) {
                if (s.getComponentsSerialized().get(id).keySet().contains(c.getComponentName())) {
                    c.deserializeFromNetwork(s.getComponentsSerialized().get(id).get(c.getComponentName()));
                }
            }
        }
        timer.setGameTime(s.getGameTime());
    }

    /**
     * Applies a snaphot delta to the system.
     * 
     * @param snapshot snaphot to apply
     * @throws JsonParseException e
     * @throws JsonMappingException e
     * @throws IOException e
     */
    public void applyNewSnapshotDelta(SnapshotDelta snapshot) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Snapshot old = getCurrentSnapshot();
        Snapshot newShot = Cloner.standard().deepClone(old);
        newShot.setGameTime(snapshot.getGameTime());
        newShot.setTick(snapshot.getTick());
        if (snapshot.getRemoved() != null) {
            for (String id : snapshot.getRemoved()) {
                entityManager.deleteEntity(id);
                newShot.getEntitiesSerialized().remove(id);
                newShot.getComponentsSerialized().remove(id);
            }
        }
        if (snapshot.getCreated() != null) {
            for (String newEntity : snapshot.getCreated()) {
                String[] values = newEntity.split(";");
                if (entityManager.getEntity(values[0]) == null) {
                    String e = entityManager.createNewEntityFromSchema(Long.parseLong(values[1]), values[0]);
                    @SuppressWarnings("unchecked") Map<String, Map<String, Double>> transformInfo =
                        mapper.readValue(values[2], new HashMap<String, Map<String, Double>>().getClass());
                    entityManager.getEntity(e).getTransform().parseTransformValues(transformInfo);
                }
            }
        }
        if (snapshot.getChanged() != null) {
            for (String entity : snapshot.getChanged().keySet()) {
                Map<String, String> components = snapshot.getChanged().get(entity);
                Map<String, Component> entityComponents = entityManager.getAllComponents(entity);
                for (Component c : entityComponents.values()) {
                    if (components.containsKey(c.getComponentName())) {
                        c.deserializeFromNetwork(components.get(c.getComponentName()));
                    }
                }
            }
        }
        currentSnapshots.add(newShot);
        if (currentSnapshots.size() > 5) {
            currentSnapshots.remove(0);
        }
        this.timer.setGameTime(newShot.getGameTime());
        this.timer.setTick(newShot.getTick());
    }
}
