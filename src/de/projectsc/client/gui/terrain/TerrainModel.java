/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui.terrain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.textures.TerrainTexture;
import de.projectsc.client.gui.textures.TerrainTexturePack;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.Maths;
import de.projectsc.core.Terrain;

/**
 * Representation of the terrain.
 * 
 * @author Josch Bosch
 */
public class TerrainModel {

    private static final Log LOGGER = LogFactory.getLog(TerrainModel.class);

    private final float xCoord;

    private final float zCoord;

    private final RawModel model;

    private final TerrainTexturePack texture;

    private final TerrainTexture blendMap;

    private Terrain terrain;

    public TerrainModel(Terrain terrain, float x, float z, TerrainTexturePack texture, TerrainTexture blendMap, Loader loader) {
        super();
        this.terrain = terrain;
        this.xCoord = x * terrain.getMapSize() * Terrain.TERRAIN_TILE_SIZE;
        this.zCoord = z * terrain.getMapSize() * Terrain.TERRAIN_TILE_SIZE;
        this.texture = texture;
        this.blendMap = blendMap;
        this.model = generateTerrainModel(loader);
    }

    private RawModel generateTerrainModel(Loader loader) {

        LOGGER.debug("Start generating terrain.");
        int vertCount = terrain.getMapSize();
        int[][] tiles = terrain.getTerrain();
        int count = vertCount * vertCount;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (vertCount - 1) * (vertCount - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < vertCount; i++) {
            for (int j = 0; j < vertCount; j++) {
                vertices[vertexPointer * 3] = j / ((float) vertCount - 1) * vertCount * Terrain.TERRAIN_TILE_SIZE;
                float height = tiles[i][j];
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = i / ((float) vertCount - 1) * vertCount * Terrain.TERRAIN_TILE_SIZE;
                Vector3f normal = calculateNormal(j, i, tiles);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = j / ((float) vertCount - 1);
                textureCoords[vertexPointer * 2 + 1] = i / ((float) vertCount - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < vertCount - 1; gz++) {
            for (int gx = 0; gx < vertCount - 1; gx++) {
                int topLeft = (gz * vertCount) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * vertCount) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    /**
     * Calculates the height of the Terrain at world position (x,z).
     * 
     * @param xWorld coordinate
     * @param zWorld coordinate
     * @return height
     */
    public float getHeightOfTerrain(float xWorld, float zWorld) {
        float terrainX = xWorld - this.xCoord;
        float terrainZ = zWorld - this.zCoord;
        float gridSize = terrain.getMapSize() / ((float) terrain.getHeights().length - 1);
        int gridX = (int) Math.floor(terrainX / gridSize);
        int gridZ = (int) Math.floor(terrainZ / gridSize);
        if (gridX >= terrain.getHeights().length - 1 || gridZ >= terrain.getHeights().length - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }
        float xCoordPlayer = (terrainX % gridSize) / gridSize;
        float zCoordPlayer = (terrainZ % gridSize) / gridSize;

        float answer = 0.0f;
        if (xCoordPlayer <= (1 - zCoordPlayer)) {
            answer = Maths.barryCentric(new Vector3f(0, terrain.getHeights()[gridX][gridZ], 0), new Vector3f(1,
                terrain.getHeights()[gridX + 1][gridZ], 0), new Vector3f(0,
                terrain.getHeights()[gridX][gridZ + 1], 1), new Vector2f(xCoordPlayer, zCoordPlayer));
        } else {
            answer = Maths.barryCentric(new Vector3f(1, terrain.getHeights()[gridX + 1][gridZ], 0), new Vector3f(1,
                terrain.getHeights()[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                terrain.getHeights()[gridX][gridZ + 1], 1), new Vector2f(xCoordPlayer, zCoordPlayer));
        }
        return answer;
    }

    private Vector3f calculateNormal(int x, int z, int[][] map) {
        float heightL = terrain.getHeight(x - 1, z);
        float heightR = terrain.getHeight(x + 1, z);
        float heightD = terrain.getHeight(x - 1, z - 1);
        float heightU = terrain.getHeight(x, z + 1);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    public float getX() {
        return xCoord;
    }

    public float getZ() {
        return zCoord;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexturePack getTexture() {
        return texture;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

}
