/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.objects.particles;

/**
 * Texture of the particles.
 * 
 * @author Josch Bosch
 */
public class ParticleTexture {

    private int textureID;

    private int numberOfRows;

    private boolean additiveBlending = false;

    public ParticleTexture(int textureID, int numberOfRows) {
        super();
        this.textureID = textureID;
        this.numberOfRows = numberOfRows;
    }

    public int getTextureID() {
        return textureID;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setAdditiveBlending(boolean additiveBlending) {
        this.additiveBlending = additiveBlending;
    }

    public boolean isAdditiveBlending() {
        return additiveBlending;
    }
}
