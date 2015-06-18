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
}
