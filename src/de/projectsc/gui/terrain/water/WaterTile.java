/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.gui.terrain.water;

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
