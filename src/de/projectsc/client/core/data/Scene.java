/*
 * Copyright (C) 2015 
 */

package de.projectsc.client.core.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Billboard;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.objects.ParticleEmitter;
import de.projectsc.client.gui.terrain.TerrainModel;
import de.projectsc.core.entities.Entity;

public class Scene {

    private List<TerrainModel> terrain;

    private Map<TexturedModel, List<Entity>> entities;

    private List<Light> lights;

    private List<ParticleEmitter> particles;

    private List<Billboard> billboards;

    public Scene() {
        terrain = new LinkedList<>();
        entities = new HashMap<>();
        lights = new LinkedList<>();
        particles = new LinkedList<>();
        billboards = new LinkedList<>();
    }

    public List<TerrainModel> getTerrain() {
        return terrain;
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

    public void setTerrain(List<TerrainModel> terrain) {
        this.terrain = terrain;
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

}
