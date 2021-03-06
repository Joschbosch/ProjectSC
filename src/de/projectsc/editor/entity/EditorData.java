/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.editor.entity;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import de.projectsc.modes.client.gui.components.MeshRendererComponent;

/**
 * Data store for the editor and the necessary {@link MeshRendererComponent}.
 * 
 * @author Josch Bosch
 */
public class EditorData {

    private int id = 10000;

    private File modelFile;

    private File textureFile;

    private float scale = 1.0f;

    private boolean transparent = true;

    private boolean fakeLighting = false;

    private float shineDamper = 1.0f;

    private float reflectivity = 0.0f;

    private int numColums = 1;

    private Set<String> componentsAdded = new HashSet<>();

    private boolean rotateCamera = false;

    private boolean cycleTextures = false;

    private boolean lightAtCamera = true;

    private boolean entityMoving = false;

    private String name = "EntitySchema0";

    private boolean renderSkybox = true;

    public EditorData() {
        modelFile = null;
        setTextureFile(null);
    }

    public File getModelFile() {
        return modelFile;
    }

    public void setModelFile(File modelFile) {
        this.modelFile = modelFile;
    }

    public File getTextureFile() {
        return textureFile;
    }

    public void setTextureFile(File textureFile) {
        this.textureFile = textureFile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public boolean isFakeLighting() {
        return fakeLighting;
    }

    public void setFakeLighting(boolean fakeLighting) {
        this.fakeLighting = fakeLighting;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public int getNumColums() {
        return numColums;
    }

    public void setNumColums(int numColums) {
        this.numColums = numColums;
    }

    public Set<String> getComponentsAdded() {
        return componentsAdded;
    }

    public void setComponentsAdded(Set<String> componentsAdded) {
        this.componentsAdded = componentsAdded;
    }

    public boolean isRotateCamera() {
        return rotateCamera;
    }

    public void setRotateCamera(boolean rotateCamera) {
        this.rotateCamera = rotateCamera;
    }

    public boolean isCycleTextures() {
        return cycleTextures;
    }

    public void setCycleTextures(boolean cycleTextures) {
        this.cycleTextures = cycleTextures;
    }

    public boolean isLightAtCameraPostion() {
        return lightAtCamera;
    }

    public void setLightAtCameraPostion(boolean value) {
        lightAtCamera = value;
    }

    public boolean isMoveEntity() {
        return entityMoving;
    }

    public void setEntityMoving(boolean value) {
        entityMoving = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRenderSkybox(boolean selected) {
        this.renderSkybox = selected;
    }

    public boolean getRenderSkybox() {
        return renderSkybox;
    }
}
