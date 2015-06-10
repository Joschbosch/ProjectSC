/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.data.content;

import java.util.HashMap;

/**
 * Represents the map.
 * 
 * @author Josch Bosch
 */
public class Map {

    private final int mapWidth;

    private final int mapHeight;

    private java.util.Map<TileType, Tile> typeMap = new HashMap<>();

    private Tile[][] map;

    public Map(int width, int height) {
        mapWidth = width;
        mapHeight = height;
        for (TileType type : TileType.values()) {
            typeMap.put(type, new Tile(type));
        }

        initMap();
    }

    private void initMap() {

        map = new Tile[mapWidth][mapHeight];
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                map[i][j] = typeMap.get(TileType.NOTHING);
            }
        }
    }

    /**
     * @param x coordinate
     * @param y coordinate
     * @return tile at (x,y)
     */
    public Tile getTileAt(int x, int y) {
        return map[x][y];
    }

    public int getWidth() {
        return mapWidth;
    }

    public int getHeight() {
        return mapHeight;
    }

    /**
     * @param i coordinate
     * @param j coordinate
     * @param type set tile at (i,j) to {@link TileType} type.
     */
    public void setTileAt(int i, int j, TileType type) {
        map[i][j] = typeMap.get(type);
    }

    /**
     * @param x coordinate
     * @param y coordinate
     * @return is (x,y) in bounds?
     */
    public boolean inBounds(int x, int y) {
        if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
            return true;
        }
        return false;
    }
}
