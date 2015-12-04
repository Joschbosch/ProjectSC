/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.gui.textures;

/**
 * A batch of terrain textures for blend mapping.
 * 
 * @author Josch Bosch
 */
public class TerrainTexturePack {

    private final TerrainTexture backgroundTexture;

    private final TerrainTexture rTexture;

    private final TerrainTexture gTexture;

    private final TerrainTexture bTexture;

    public TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture rTexture, TerrainTexture gTexture, TerrainTexture bTexture) {
        super();
        this.backgroundTexture = backgroundTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
    }

    public TerrainTexture getBackgroundTexture() {
        return backgroundTexture;
    }

    public TerrainTexture getRTexture() {
        return rTexture;
    }

    public TerrainTexture getGTexture() {
        return gTexture;
    }

    public TerrainTexture getBTexture() {
        return bTexture;
    }

}
