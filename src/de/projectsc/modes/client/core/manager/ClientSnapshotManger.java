/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;

public class ClientSnapshotManger {

    private List<Snapshot> currentSnapshots = new ArrayList<>();

    private EntityManager entityManager;

    private Timer timer;

    public ClientSnapshotManger(EntityManager entityManager, Timer timer) {
        this.entityManager = entityManager;
        this.timer = timer;
    }

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

    public void applyNewAknowledgedSnapshot(Snapshot s) {
        currentSnapshots.add(s);
        deserializeSnapshot(s);
        if (currentSnapshots.size() > 5) {
            currentSnapshots.remove(0);
        }

    }

    private void deserializeSnapshot(Snapshot s) {
        // if (s.getTick() != timer.getTick()) {
        // System.out.println("TICKS DON'T MATCH!" + s.getTick() + "   " + timer.getTick() + " diifff : "
        // + (s.getTick() - timer.getTick()));
        // }
        // if (s.getGameTime() != timer.getGameTime()) {
        // System.out.println("GAMETIMES DON'T MATCH!" + s.getTick() + "   " + timer.getTick() + " diifff : "
        // + (s.getTick() - timer.getTick()));
        // }
        for (String id : s.getEntitiesSerialized().keySet()) {
            if (entityManager.getEntity(id) == null) {

            } else {
                Map<String, Component> components = entityManager.getAllComponents(id);
                for (Component c : components.values()) {
                    System.out.println(s.getComponentsSerialized().keySet());
                    if (s.getComponentsSerialized().get(id).keySet().contains(c.getComponentName())) {
                        c.deserializeFromNetwork(s.getComponentsSerialized().get(id).get(c.getComponentName()));
                    }
                }

            }
        }
        timer.setGameTime(s.getGameTime());
    }
}
