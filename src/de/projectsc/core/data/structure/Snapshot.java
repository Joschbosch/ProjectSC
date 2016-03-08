/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.data.structure;

import java.util.HashMap;
import java.util.Map;

import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.Entity;

/**
 * Snapshot of the current game state.
 * 
 * @author Josch Bosch
 */
public class Snapshot {

    private long gameTime;

    private long tick;

    private Map<String, String> entitiesSerialized;

    private Map<String, Map<String, String>> componentsSerialized;

    /**
     * Add new data to the snapshot.
     * 
     * @param newGameTime game time to set.
     * @param newTick to set.
     * @param entities to set.
     * @param entityComponents to set.
     */
    public void addData(long newGameTime, long newTick, Map<String, Entity> entities,
        Map<String, Map<String, Component>> entityComponents) {
        this.gameTime = newGameTime;
        this.tick = newTick;
        entitiesSerialized = new HashMap<>();
        componentsSerialized = new HashMap<>();
        for (String id : entities.keySet()) {
            Entity e = entities.get(id);
            String serial = "" + e.getEntityTypeId() + ";" + e.getTag() + ";" + e.getLayer();
            entitiesSerialized.put(id, serial);
            Map<String, String> components = new HashMap<>();
            for (Component c : entityComponents.get(id).values()) {
                String serialComponent = c.serializeForNetwork();
                if (!serial.isEmpty()) {
                    components.put(c.getComponentName(), serialComponent);
                }
            }
            componentsSerialized.put(id, components);
        }
    }

    public long getGameTime() {
        return gameTime;
    }

    public long getTick() {
        return tick;
    }

    @Override
    public String toString() {
        return String.format("Snapshot@%s (%s): Entities: %s ; Components: %s", tick, gameTime, entitiesSerialized, componentsSerialized);
    }

    public Map<String, String> getEntitiesSerialized() {
        return entitiesSerialized;
    }

    public void setEntitiesSerialized(Map<String, String> entitiesSerialized) {
        this.entitiesSerialized = entitiesSerialized;
    }

    public Map<String, Map<String, String>> getComponentsSerialized() {
        return componentsSerialized;
    }

    public void setComponentsSerialized(Map<String, Map<String, String>> componentsSerialized) {
        this.componentsSerialized = componentsSerialized;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public void setTick(long tick) {
        this.tick = tick;
    }
    /**
     * Clone current snapshot. 
     * @return new cloned snapshot.
     */
    public Snapshot cloneSnapshot() {
        Snapshot newSnapshot = new Snapshot();
        newSnapshot.setGameTime(gameTime);
        newSnapshot.setTick(tick);
        Map<String, Map<String, String>> newComponents = new HashMap<>();
        newComponents.putAll(componentsSerialized);
        newSnapshot.setComponentsSerialized(newComponents);
        Map<String, String> newEntities = new HashMap<>();
        newEntities.putAll(entitiesSerialized);
        newSnapshot.setEntitiesSerialized(newEntities);
        return newSnapshot;
    }

}
