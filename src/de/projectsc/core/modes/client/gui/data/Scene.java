/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.BoundingBox;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.modes.client.gui.models.RawModel;
import de.projectsc.core.modes.client.gui.models.TexturedModel;
import de.projectsc.core.modes.client.gui.objects.billboards.Billboard;
import de.projectsc.core.modes.client.gui.objects.particles.ParticleEmitter;
import de.projectsc.core.modes.client.gui.objects.terrain.TerrainModel;

/**
 * The scene contains all elemets, that need to be rendered by the GUI. It will collect all neccessary information from all entities and the
 * world itself.
 * 
 * @author Josch Bosch
 */
public class Scene {

    private List<TerrainModel> terrains;

    private Map<TexturedModel, List<Long>> models;

    private Map<Long, Float> scales;

    private Map<Long, Vector3f> positions;

    private Map<Long, Vector3f> rotations;

    private List<Light> lights;

    private List<ParticleEmitter> particles;

    private List<Billboard> billboards;

    private Map<RawModel, List<BoundingBox>> boundingBoxModels;

    private boolean renderSkybox = true;

    private Vector3f fogColor = null;

    private boolean wireframeEnabled = false;

    private Vector3f skyColor = null;

    public Scene() {
        terrains = new LinkedList<>();
        models = new HashMap<>();
        lights = new LinkedList<>();
        particles = new LinkedList<>();
        billboards = new LinkedList<>();
        boundingBoxModels = new HashMap<>();
        scales = new HashMap<>();
        rotations = new HashMap<>();
        positions = new HashMap<>();
    }

    public List<TerrainModel> getTerrains() {
        return terrains;
    }

    public Map<TexturedModel, List<Long>> getModels() {
        return models;
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

    public void setModels(Map<TexturedModel, List<Long>> models) {
        this.models = models;
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

    public Map<Long, Float> getScales() {
        return scales;
    }

    public void setScales(Map<Long, Float> scale) {
        this.scales = scale;
    }

    public Map<Long, Vector3f> getPositions() {
        return positions;
    }

    public void setPositions(Map<Long, Vector3f> positions) {
        this.positions = positions;
    }

    public Map<Long, Vector3f> getRotations() {
        return rotations;
    }

    public void setRotations(Map<Long, Vector3f> rotations) {
        this.rotations = rotations;
    }

    /**
     * Determines if the skybox should be rendered.
     * 
     * @return true if it should
     */
    public boolean renderSkyBox() {
        return renderSkybox;
    }

    public void setRenderSkybox(boolean renderSkybox) {
        this.renderSkybox = renderSkybox;
    }

    public Vector3f getFogColor() {
        return fogColor;
    }

    public void setFogColor(Vector3f fogColor) {
        this.fogColor = fogColor;
    }

    public boolean isWireframeEnable() {
        return wireframeEnabled;
    }

    public void setWireframeEnabled(boolean wireframeEnabled) {
        this.wireframeEnabled = wireframeEnabled;
    }

    public Vector3f getSkyColor() {
        return skyColor;
    }

    public void setSkyColor(Vector3f skyColor) {
        this.skyColor = skyColor;
    }
}
