/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Snapshot for the client.
 * 
 * @author Josch Bosch
 */
public class SnapshotDelta {

    private long tick;

    private long gameTime;

    private Set<String> created = null;

    private Set<String> removed = null;

    private Map<String, Map<String, String>> changed = null;

    /**
     * Removed entities.
     * 
     * @param uid removed.
     */
    public void addRemoved(String uid) {
        if (removed == null) {
            removed = new HashSet<>();
        }
        removed.add(uid);
    }

    /**
     * New created entity.
     * 
     * @param newEntityInformation new entitiy
     */
    public void addCreated(String newEntityInformation) {
        if (created == null) {
            created = new HashSet<>();
        }
        created.add(newEntityInformation);
    }

    /**
     * Changed entities.
     * 
     * @param uid of entity
     * @param componentChanges changes
     */
    public void addChange(String uid, Map<String, String> componentChanges) {
        if (changed == null) {
            changed = new HashMap<>();
        }
        changed.put(uid, componentChanges);
    }

    public void setTick(long tick) {
        this.tick = tick;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public long getTick() {
        return tick;
    }

    public long getGameTime() {
        return gameTime;
    }

    public Set<String> getRemoved() {
        return removed;
    }

    public Map<String, Map<String, String>> getChanged() {
        return changed;
    }

    public void setChanged(Map<String, Map<String, String>> changed) {
        this.changed = changed;
    }

    public Set<String> getCreated() {
        return created;
    }
}
