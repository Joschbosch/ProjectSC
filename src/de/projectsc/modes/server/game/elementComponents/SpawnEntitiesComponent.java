/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.server.game.elementComponents;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.interfaces.Component;

/**
 * Add ability to spawn entities to the component.
 * 
 * @author Josch Bosch
 */
public class SpawnEntitiesComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Spawn Entities Component";

    private Map<Long, List<Vector3f>> entitySpawnLocations = new HashMap<>();

    private double spawnIntervall = 30000; // time in ms

    private int numberOfSpawns = -1; // -1 -> forever

    private int followPathId = 0; // -1 -> forever

    private double timer = 0;

    public SpawnEntitiesComponent() {
        setType(ComponentType.GAME);
        setComponentName(NAME);
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        Map<String, Object> result = new HashMap<>();
        result.put("spawnIntervall", spawnIntervall);
        result.put("numberOfSpawns", numberOfSpawns);
        result.put("entitySpawnLocations", entitySpawnLocations);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(Map<String, Object> serialized, String loadingLocation) {
        spawnIntervall = (double) serialized.get("spawnIntervall");
        numberOfSpawns = (int) serialized.get("numberOfSpawns");
        entitySpawnLocations = (Map<Long, List<Vector3f>>) serialized.get("entitySpawnLocations");
        List<Vector3f> location = new LinkedList<>();
        location.add(new Vector3f());
        entitySpawnLocations.put(10002L, location);
    }

    @Override
    public Component cloneComponent() {
        SpawnEntitiesComponent sec = new SpawnEntitiesComponent();
        sec.getEntitySpawnLocations().putAll(entitySpawnLocations);
        sec.setNumberOfSpawns(numberOfSpawns);
        sec.setSpawnIntervall(spawnIntervall);
        return sec;
    }
    /**
     * New spawn point for entities. 
     * @param schemaId to load
     * @param location to spawn.
     */
    public void addEntitySpawnPoint(Long schemaId, Vector3f location) {
        List<Vector3f> locations = entitySpawnLocations.get(schemaId);
        if (locations == null) {
            locations = new LinkedList<>();
            entitySpawnLocations.put(schemaId, locations);
        }
        locations.add(location);
    }

    public Map<Long, List<Vector3f>> getEntitySpawnLocations() {
        return entitySpawnLocations;
    }

    public void setEntitySpawnLocations(Map<Long, List<Vector3f>> entitySpawnLocations) {
        this.entitySpawnLocations = entitySpawnLocations;
    }

    public double getSpawnIntervall() {
        return spawnIntervall;
    }

    public void setSpawnIntervall(double spawnIntervall) {
        this.spawnIntervall = spawnIntervall;
    }

    public int getNumberOfSpawns() {
        return numberOfSpawns;
    }

    public void setNumberOfSpawns(int numberOfSpawns) {
        this.numberOfSpawns = numberOfSpawns;
    }

    public double getTimer() {
        return timer;
    }

    public void setTimer(double timer) {
        this.timer = timer;
    }

    public int getFollowPathId() {
        return followPathId;
    }

    public void setFollowPathId(int followPathId) {
        this.followPathId = followPathId;
    }
}
