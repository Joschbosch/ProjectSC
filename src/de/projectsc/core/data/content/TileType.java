/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.data.content;

/**
 * All kinds of tile types.
 * 
 * @author Josch Bosch
 */
public enum TileType {
    /**
    * 
    */
    NOTHING(68, new float[] { 0.0f, 0.0f, 0.0f }),
    /**
    * 
    */
    GRAS(946, new float[] { 0.0f, 1.0f, 0.0f }),
    /**
    * 
    */
    WATER(1217, new float[] { 0.0f, 0.0f, 1.0f }),
    /**
    * 
    */
    ROCK(883, new float[] { 0.5f, 0.35f, 0.05f });

    private final int tileId;

    private float[] color;

    TileType(int id, float[] color) {
        this.tileId = id;
        this.color = color;
    }

    public int getTileId() {
        return tileId;
    }

    public float[] getColor() {
        return color;
    }

}
