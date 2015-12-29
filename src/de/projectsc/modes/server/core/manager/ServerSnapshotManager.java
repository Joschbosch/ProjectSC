/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.server.core.manager;

import java.util.HashMap;
import java.util.Map;

import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.modes.server.core.ServerConstants;

public class ServerSnapshotManager {

    private Snapshot[] snapshots;

    private int lastSnapshot = 0;

    private EntityManager enitityManager;

    private Map<String, Long> lastSnapshotsSend;

    public ServerSnapshotManager(EntityManager entityManager) {
        this.snapshots = new Snapshot[ServerConstants.MAXIMUM_STORES_SNAPSHOTS];
        this.enitityManager = entityManager;
        this.lastSnapshotsSend = new HashMap<>();
    }

    public long getLastSendSnapshotTick(String clientId) {
        if (lastSnapshotsSend.containsKey(clientId)) {
            return lastSnapshotsSend.get(clientId);
        } else {
            return -1;
        }
    }

    public void createSnapshot(Timer timer) {
        if (lastSnapshot == snapshots.length - 1) {
            lastSnapshot = 0;
        } else {
            lastSnapshot++;
        }
        // Map<String, Entity> entities = Cloner.standard().deepClone(enitityManager.getEntites());
        // Map<String, Map<String, Component>> entityComponents = Cloner.standard().deepClone(enitityManager.getEntityComponents());

        Snapshot s = new Snapshot();
        s.addData(timer.getGameTime(), timer.getTick(), enitityManager.getEntites(), enitityManager.getEntityComponents());
        snapshots[lastSnapshot] = s;
    }

    public Snapshot getLastSnapshot() {
        if (snapshots[lastSnapshot] != null) {
            return snapshots[lastSnapshot];
        }
        return null;
    }

    public void setLastSnapshotSendTick(String id, long tick) {
        lastSnapshotsSend.put(id, tick);
    }

    public Snapshot getSnapshot(long snapshotTick) {
        for (Snapshot s : snapshots) {
            if (s.getTick() == snapshotTick) {
                return s;
            }
        }
        return null;
    }

}
