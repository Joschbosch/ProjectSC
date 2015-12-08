/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.projectsc.core.data.BoundingBox;
import de.projectsc.core.data.entities.Entity;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.modes.client.gui.models.RawModel;
import de.projectsc.core.modes.client.gui.models.TexturedModel;
import de.projectsc.core.modes.client.gui.objects.billboards.Billboard;
import de.projectsc.core.modes.client.gui.objects.particles.ParticleEmitter;
import de.projectsc.core.modes.client.gui.objects.terrain.TerrainModel;

/**
 * The scene contains all elemets, that need to be rendered by the GUI. It will collect all
 * neccessary information from all entities and the world itself.
 * 
 * @author Josch Bosch
 */
public class Scene {

    private List<TerrainModel> terrains;

    private Map<TexturedModel, List<Entity>> entities;

    private List<Light> lights;

    private List<ParticleEmitter> particles;

    private List<Billboard> billboards;

    private Map<RawModel, List<BoundingBox>> boundingBoxModels;

    public Scene() {
        terrains = new LinkedList<>();
        entities = new HashMap<>();
        lights = new LinkedList<>();
        particles = new LinkedList<>();
        billboards = new LinkedList<>();
        boundingBoxModels = new HashMap<>();
    }

    public List<TerrainModel> getTerrain() {
        return terrains;
    }

    public Map<TexturedModel, List<Entity>> getEntities() {
        return entities;
    }

    public List<Light> getLights() {
        return lights;
    }

    public List<Billboard> getBillboards() {
        return billboards;
    }

    public List<ParticleEmitter> getParticles() {
        return particles;
    }

    public Map<RawModel, List<BoundingBox>> getBoundingBoxModels() {
        return boundingBoxModels;
    }

    public void setTerrains(List<TerrainModel> terrains) {
        this.terrains = terrains;
    }

    public void setEntities(Map<TexturedModel, List<Entity>> entities) {
        this.entities = entities;
    }

    public void setLights(List<Light> lights) {
        this.lights = lights;
    }

    public void setParticles(List<ParticleEmitter> particles) {
        this.particles = particles;
    }

    public void setBillboards(List<Billboard> billboards) {
        this.billboards = billboards;
    }

    public void setBoundingBoxModels(Map<RawModel, List<BoundingBox>> boundingBoxModels) {
        this.boundingBoxModels = boundingBoxModels;
    }
}
