/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core.algorithms;

/**
 * Create a new room.
 * 
 * @author Josch Bosch
 */
public class Room {

    private final int[][] roomTiles;

    public Room(int width, int height) {
        roomTiles = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                getRoomTiles()[i][j] = 1;
            }
        }
    }

    /**
     * @param x coordinate
     * @param y coordinate
     * @return check if tile at (x,y) is free.
     */
    public boolean isFree(int x, int y) {
        return getRoomTiles()[x][y] == 1;
    }

    /**
     * @param x coordinate
     * @param y coordinate
     * @return check if tile at (x,y) is solid.
     */
    public boolean isSolid(int x, int y) {
        return getRoomTiles()[x][y] == 0;
    }

    public int[][] getRoomTiles() {
        return roomTiles;
    }
}
