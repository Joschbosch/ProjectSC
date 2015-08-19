/*
 * Copyright (C) 2015
 */

package de.projectsc.core;

import java.util.List;
import java.util.Map;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Billboard;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.objects.ParticleEmitter;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.utils.BoundingBox;

/**
 * Defines the whole world.
 * 
 * @author Josch Bosch
 */
public class World {

    private final Terrain[][] terrains;

    private Map<TexturedModel, List<Entity>> entities;

    private Map<RawModel, List<BoundingBox>> boundingBoxes;

    private List<Light> lights;

    private List<Billboard> billboards;

    private List<ParticleEmitter> particles;

    public World(int sizeX, int sizeY, String startTexture) {
        terrains = new Terrain[sizeX][sizeY];
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                terrains[i][i] = new Terrain(i * Terrain.TERRAIN_TILE_SIZE * Terrain.TERRAIN_TILE_SIZE,
                    j * Terrain.TERRAIN_TILE_SIZE * Terrain.TERRAIN_TILE_SIZE, startTexture, startTexture, startTexture, startTexture);
            }
        }
    }

    public Terrain getTerrainAt(float worldX, float worldZ) {
        int terrainX = (int) (worldX / (Terrain.TERRAIN_TILE_SIZE * Terrain.TERRAIN_CHUNK_SIZE));
        int terrainZ = (int) (worldZ / (Terrain.TERRAIN_TILE_SIZE * Terrain.TERRAIN_CHUNK_SIZE));
        if (terrainX >= 0 && terrainX < terrains.length && terrainZ >= 0 && terrainZ < terrains[0].length) {
            return terrains[terrainX][terrainZ];
        }
        return null;
    }

    public Tile getTileAt(float worldX, float worldZ) {
        Terrain t = getTerrainAt(worldX, worldZ);
        if (t != null) {
            float terrainX = worldX - t.getWorldPositionX();
            float terrainZ = worldZ - t.getWorldPositionZ();
            int positionX = (int) (terrainX / Terrain.TERRAIN_TILE_SIZE);
            int positionZ = (int) (terrainZ / Terrain.TERRAIN_TILE_SIZE);
            if (positionX >= 0 && positionX < Terrain.TERRAIN_CHUNK_SIZE && positionZ >= 0 && positionZ < Terrain.TERRAIN_CHUNK_SIZE) {
                return t.getTerrain()[positionX][positionZ];
            }

        }
        return null;
    }

}
