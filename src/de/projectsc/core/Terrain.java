/*
 * Copyright (C) 2015 
 */

package de.projectsc.core;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.Light;

public class Terrain {

    private static final Log LOGGER = LogFactory.getLog(Terrain.class);

    /**
     * Factor for a terrain offset. (1 tile has offset [0, TERRAIN_TILE_SIZE])
     */
    public static final float TERRAIN_TILE_SIZE = 4.0f;

    private int mapSize;

    private int[][] tiles;

    private float[][] heights;

    private String bgTexture;

    private String rTexture;

    private String gTexture;

    private String bTexture;

    private List<Light> staticLights;

    public Terrain(int[][] tiles, float[][] heights, String bgTexture, String rTexture, String gTexture, String bTexture, List<Light> lights) {
        this.tiles = tiles;
        this.mapSize = tiles.length;
        this.heights = heights;
        this.bgTexture = bgTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
        this.setStaticLights(lights);
    }

    public Vector3f calculateNormal(int x, int z) {
        float heightL = getHeight(x - 1, z);
        float heightR = getHeight(x + 1, z);
        float heightD = getHeight(x - 1, z - 1);
        float heightU = getHeight(x, z + 1);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

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

    public String getrTexture() {
        return rTexture;
    }

    public String getgTexture() {
        return gTexture;
    }

    public String getbTexture() {
        return bTexture;
    }

    public List<Light> getStaticLights() {
        return staticLights;
    }

    public void setStaticLights(List<Light> staticLights) {
        this.staticLights = staticLights;
    }
}
