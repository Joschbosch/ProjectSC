/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.textures;

/**
 * Represents the texture for a model.
 * 
 * @author Josch Bosch
 */
public class ModelTexture {

    private int textureID;

    private float shineDamper = 1;

    private float reflectivity = 0;

    private boolean transparent = false;

    private boolean fakeLighting = false;

    private int numberOfRows = 1;

    private int activeTextureIndex;

    private int normalMap = -1;

    public ModelTexture(int textureID) {
        super();
        this.textureID = textureID;
    }

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
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

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparency) {
        this.transparent = transparency;
    }

    public boolean isFakeLighting() {
        return fakeLighting;
    }

    public void setFakeLighting(boolean fakeLighting) {
        this.fakeLighting = fakeLighting;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public void setActiveTextureIndex(int textureIndex) {
        this.activeTextureIndex = textureIndex;
    }

    public int getActiveTextureIndex() {
        return activeTextureIndex;
    }

    public int getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(int normalMap) {
        this.normalMap = normalMap;
    }
}
