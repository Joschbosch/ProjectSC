/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.physics.WireFrame;
import de.projectsc.modes.client.gui.models.AnimatedModel;
import de.projectsc.modes.client.gui.models.TexturedModel;
import de.projectsc.modes.client.gui.objects.billboards.Billboard;
import de.projectsc.modes.client.gui.objects.terrain.TerrainModel;

/**
 * The scene contains all elemets, that need to be rendered by the GUI. It will collect all neccessary information from all entities and the
 * world itself.
 * 
 * @author Josch Bosch
 */
public class GUIScene {

    private List<TerrainModel> terrains;

    private Map<TexturedModel, List<String>> models;

    private Map<String, Vector3f> scales;

    private Map<String, Vector3f> positions;

    private Map<String, Vector3f> rotations;

    private List<Light> lights;

    private List<Billboard> billboards;

    private boolean renderSkybox = true;

    private Vector3f fogColor = null;

    private Vector3f skyColor = null;

    private boolean wireframeEnabled = true;

    private boolean isDebugMode = false;

    private List<WireFrame> wireFrames = new LinkedList<>();

    private List<Vector3f> selectedEntites = new LinkedList<>();

    private List<Vector3f> highlightedEntities = new LinkedList<>();

    private Map<AnimatedModel, List<String>> animatedModels;

    public GUIScene() {
        super();
        terrains = new LinkedList<>();
        models = new HashMap<>();
        lights = new LinkedList<>();
        billboards = new LinkedList<>();
        scales = new HashMap<>();
        rotations = new HashMap<>();
        positions = new HashMap<>();
        animatedModels = new HashMap<>();

    }

    public List<TerrainModel> getTerrains() {
        return terrains;
    }

    public Map<TexturedModel, List<String>> getModels() {
        return models;
    }

    public Map<AnimatedModel, List<String>> getAnimatedModels() {
        return animatedModels;
    }

    public List<Light> getLights() {
        return lights;
    }

    public List<Billboard> getBillboards() {
        return billboards;
    }

    public void setTerrains(List<TerrainModel> terrains) {
        this.terrains = terrains;
    }

    public void setModels(Map<TexturedModel, List<String>> models) {
        this.models = models;
    }

    public void setLights(List<Light> lights) {
        this.lights = lights;
    }

    public void setBillboards(List<Billboard> billboards) {
        this.billboards = billboards;
    }

    public Map<String, Vector3f> getScales() {
        return scales;
    }

    public void setScales(Map<String, Vector3f> scale) {
        this.scales = scale;
    }

    public Map<String, Vector3f> getPositions() {
        return positions;
    }

    public void setPositions(Map<String, Vector3f> positions) {
        this.positions = positions;
    }

    public Map<String, Vector3f> getRotations() {
        return rotations;
    }

    public void setRotations(Map<String, Vector3f> rotations) {
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

    public Vector3f getSkyColor() {
        return skyColor;
    }

    public void setSkyColor(Vector3f skyColor) {
        this.skyColor = skyColor;
    }

    public boolean isWireframeEnabled() {
        return wireframeEnabled;
    }

    public void setWireframeEnabled(boolean wireframeEnabled) {
        this.wireframeEnabled = wireframeEnabled;
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.isDebugMode = debugMode;
    }

    public List<WireFrame> getWireFrames() {
        return wireFrames;
    }

    public void setWireFrames(List<WireFrame> wireFrames) {
        this.wireFrames = wireFrames;
    }

    public List<Vector3f> getSelectedEntites() {
        return selectedEntites;
    }

    public void setSelectedEntites(List<Vector3f> selectedEntites) {
        this.selectedEntites = selectedEntites;
    }

    public List<Vector3f> getHightlightedEntites() {
        return highlightedEntities;
    }

    public void setHighlightedEntities(List<Vector3f> highlightedEntities) {
        this.highlightedEntities = highlightedEntities;
    }

    public boolean isRenderSkybox() {
        return renderSkybox;
    }

}
