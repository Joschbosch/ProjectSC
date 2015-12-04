/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.gui.textures;

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
}
