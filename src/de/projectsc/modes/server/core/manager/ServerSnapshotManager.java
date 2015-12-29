/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.server.core.manager;

import java.util.HashMap;
import java.util.Map;

import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.data.structure.SnapshotDelta;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.modes.server.core.ServerConstants;

/**
 * Manager for snapshtots on the server.
 * 
 * @author Josch Bosch
 */
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

    /**
     * When was the last snapshot sent.
     * 
     * @param clientId to which was was sent
     * @return tick
     */
    public long getLastSendSnapshotTick(String clientId) {
        if (lastSnapshotsSend.containsKey(clientId)) {
            return lastSnapshotsSend.get(clientId);
        } else {
            return -1;
        }
    }

    /**
     * Create a new snapshot.
     * 
     * @param timer for tick time
     */
    public void createSnapshot(Timer timer) {
        if (lastSnapshot == snapshots.length - 1) {
            lastSnapshot = 0;
        } else {
            lastSnapshot++;
        }
        Snapshot s = new Snapshot();
        s.addData(timer.getGameTime(), timer.getTick(), enitityManager.getEntites(), enitityManager.getEntityComponents());
        snapshots[lastSnapshot] = s;
    }

    /**
     * Returns the current snapshot.
     * 
     * @return curren sn
     */
    public Snapshot getLastSnapshot() {
        if (snapshots[lastSnapshot] != null) {
            return snapshots[lastSnapshot];
        }
        return null;
    }

    /**
     * Sets the last snapshot sent to id.
     * 
     * @param id of client
     * @param tick to set
     */

    public void setLastSnapshotSendTick(String id, long tick) {
        lastSnapshotsSend.put(id, tick);
    }

    /**
     * @param snapshotTick to get
     * @return snapshot with given tick
     */

    public Snapshot getSnapshot(long snapshotTick) {
        for (int i = 0; i < snapshots.length; i++) {
            if (snapshots[i] != null && snapshots[i].getTick() == snapshotTick) {
                return snapshots[i];
            }
        }
        return null;
    }

    /**
     * Create a diff from two snapshots.
     * 
     * @param lastSnapshotSend last
     * @param currentSnapshot current
     * @return delta snapshot
     */
    public SnapshotDelta createSnapshotDelta(Snapshot lastSnapshotSend, Snapshot currentSnapshot) {
        SnapshotDelta delta = new SnapshotDelta();
        delta.setGameTime(currentSnapshot.getGameTime());
        delta.setTick(currentSnapshot.getTick());
        for (String entityId : currentSnapshot.getComponentsSerialized().keySet()) {
            Map<String, String> components = currentSnapshot.getComponentsSerialized().get(entityId);
            Map<String, String> componentsOld = lastSnapshotSend.getComponentsSerialized().get(entityId);
            Map<String, String> componentsDelta = new HashMap<>();
            if (componentsOld != null) {
                for (String comp : components.keySet()) {
                    if (!components.get(comp).equals(componentsOld.get(comp))) {
                        componentsDelta.put(comp, components.get(comp));
                    }
                }
            }
            if (componentsDelta.size() > 0) {
                delta.addChange(entityId, componentsDelta);
            }
        }
        return delta;
    }
}
