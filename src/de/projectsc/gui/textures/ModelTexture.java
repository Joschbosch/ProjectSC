/*
 * /* Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.textures;

/**
 * Represents the texture for a model.
 * 
 * @author Josch Bosch
 */
public class ModelTexture {

    private int textureID;

    private float shineDamper = 1;

    private float reflectivity = 0;

    private boolean hasTransparency = false;

    private boolean fakeLighting = false;

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

    public boolean hasTransparency() {
        return hasTransparency;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public boolean isFakeLighting() {
        return fakeLighting;
    }

    public void setFakeLighting(boolean fakeLighting) {
        this.fakeLighting = fakeLighting;
    }
}
