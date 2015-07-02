/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.core;

import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.Light;
import de.projectsc.core.entities.WorldEntity;
import de.projectsc.core.utils.BoundingBox;

/**
 * Holds all information about the static terrain.
 * 
 * @author Josch Bosch
 */
public class Terrain {

    /**
     * Factor for a terrain offset. (1 tile has offset [0, TERRAIN_TILE_SIZE])
     */
    public static final float TERRAIN_TILE_SIZE = 4.0f;

    private final int mapSize;

    private final int[][] tiles;

    private final float[][] heights;

    private final String bgTexture;

    private final String rTexture;

    private final String gTexture;

    private final String bTexture;

    private List<Light> staticLights;

    private Map<Integer, WorldEntity> staticObjects;

    private BoundingBox mapBox;

    public Terrain(int[][] tiles, float[][] heights, String bgTexture, String rTexture, String gTexture,
        String bTexture, List<Light> lights, Map<Integer, WorldEntity> staticObjects) {
        this.tiles = tiles;
        this.mapSize = tiles.length;
        this.heights = heights;
        this.bgTexture = bgTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
        this.setStaticLights(lights);
        this.staticObjects = staticObjects;
        this.setMapBox(calculateMapBox());
    }

    private BoundingBox calculateMapBox() {
        Vector3f minimum = new Vector3f(0, 0, 0);
        Vector3f maximum =
            new Vector3f(mapSize * Terrain.TERRAIN_TILE_SIZE, mapSize * Terrain.TERRAIN_TILE_SIZE, mapSize * Terrain.TERRAIN_TILE_SIZE);

        return new BoundingBox(minimum, maximum);
    }

    /**
     * Calculate normal vector of tile [x,y].
     * 
     * @param x coordinate.
     * @param z coordinate.
     * @return normal vector
     */
    public Vector3f calculateNormal(int x, int z) {
        float heightL = getHeight(x - 1, z);
        float heightR = getHeight(x + 1, z);
        float heightD = getHeight(x - 1, z - 1);
        float heightU = getHeight(x, z + 1);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    /**
     * Return height of coordinate [x,z].
     * 
     * @param x coordinate
     * @param z coordinate
     * @return height
     */
    public float getHeight(int x, int z) {
        if (x >= 0 && x < heights.length && z >= 0 && z < heights[0].length) {
            return heights[x][z];
        }
        return 0;
        // if (x < 0 || x >= map.getHeight() || z < 0 || z >= map.getWidth()) {
        // return 0;
        // }
        // float height = map.getRGB(x, z);
        // height += MAX_PIXEL_COLOR / 2f;
        // height /= MAX_PIXEL_COLOR / 2f;
        // height *= MAXIMUM_HEIGHT;
        // return height;
    }

    public float[][] getHeights() {
        return heights;
    }

    public int getMapSize() {
        return mapSize;
    }

    public int[][] getTerrain() {
        return tiles;
    }

    public String getBgTexture() {
        return bgTexture;
    }

    public String getRTexture() {
        return rTexture;
    }

    public String getGTexture() {
        return gTexture;
    }

    public String getBTexture() {
        return bTexture;
    }

    public List<Light> getStaticLights() {
        return staticLights;
    }

    public void setStaticLights(List<Light> staticLights) {
        this.staticLights = staticLights;
    }

    public Map<Integer, WorldEntity> getStaticObjects() {
        return staticObjects;
    }

    public void setStaticObjects(Map<Integer, WorldEntity> staticObjects) {
        this.staticObjects = staticObjects;
    }

    public BoundingBox getMapBoundingBox() {
        return mapBox;
    }

    public void setMapBox(BoundingBox mapBox) {
        this.mapBox = mapBox;
    }
}
