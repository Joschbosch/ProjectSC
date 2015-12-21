/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.objects.terrain.water;

/**
 * A tile of water.
 * 
 * @author Josch Bosch
 */
public class WaterTile {

    /**
     * Size of one water tile.
     */
    public static final float TILE_SIZE = 60;

    private final float height;

    private final float x;

    private final float z;

    public WaterTile(float centerX, float centerZ, float height) {
        this.x = centerX;
        this.z = centerZ;
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

}
