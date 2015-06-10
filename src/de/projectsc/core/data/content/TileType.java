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
    NOTHING(0), GRAS(1), WATER(2), ROCK(3);

    private final int representation;

    TileType(int representation) {
        this.representation = representation;
    }

    public int getRepresentation() {
        return representation;
    }
}
